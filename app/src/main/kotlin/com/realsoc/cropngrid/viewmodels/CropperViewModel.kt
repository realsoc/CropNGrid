package com.realsoc.cropngrid.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.realsoc.cropngrid.data.GridRepository
import com.realsoc.cropngrid.data.PictureRepository
import com.realsoc.cropngrid.createBitmapOfArea
import com.realsoc.cropngrid.encode
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.navigation.CropperArgs
import com.realsoc.cropngrid.safeRecycle
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import com.realsoc.cropngrid.ui.models.GridParameters
import com.realsoc.cropngrid.ui.scale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CropperViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val pictureRepository: PictureRepository,
    private val gridRepository: GridRepository
): AndroidViewModel(application) {

    private val cropArgs: CropperArgs = CropperArgs(savedStateHandle)

    private val _croppingUiState = MutableStateFlow<CroppingUiState?>(null)

    val croppingUiState: StateFlow<CroppingUiState?> = _croppingUiState.asStateFlow()

    val pictureUri: Uri = cropArgs.uri

    private val _gridParametersState: MutableStateFlow<GridParameters> = MutableStateFlow(GridParameters())
    val gridParametersState: StateFlow<GridParameters> = _gridParametersState.asStateFlow()

    fun makeGrid(
        source: Bitmap,
        miniatureArea: Rect,
        pParts: List<List<Rect>>,
        pCoordinateSystem: CoordinateSystem,
        baseName: String?
    ) {
        // A crop is already running (e.g. double tap on confirm): don't start a duplicate
        if (_croppingUiState.value == CroppingUiState.Loading) return
        _croppingUiState.update { CroppingUiState.Loading }

        viewModelScope.launch {
            val createdBitmaps = mutableListOf<Bitmap>()
            val savedUris = mutableListOf<Uri>()
            try {
                var coordinateSystem = pCoordinateSystem
                var parts = pParts
                val timestamp = System.currentTimeMillis()

                @Suppress("NAME_SHADOWING") val baseName = (baseName ?: "UNKNOWN") + "_" + timestamp

                val suffixName = baseName + "_"

                val miniatureBitmap = createBitmapOfArea(source, miniatureArea, coordinateSystem)
                    .also { createdBitmaps += it }
                val miniatureUri = pictureRepository.saveImage(
                    getApplication(),
                    miniatureBitmap,
                    suffixName + "MINIATURE"
                ).also { savedUris += it }

                if (pCoordinateSystem.transformation.scale != 0f && pCoordinateSystem.transformation.scale < 1f) {
                    val newScale = 1 / pCoordinateSystem.transformation.scale
                    coordinateSystem =
                        pCoordinateSystem.withTransformation(pCoordinateSystem.transformation.scale(newScale))
                    parts = parts.map { row -> row.map { it.scale(newScale, pCoordinateSystem.pivot) } }
                }

                val partEncodedUris = parts.mapIndexed { rowNumber, row ->
                    row.mapIndexed { columnNumber, part ->
                        val bitmap = createBitmapOfArea(source, part, coordinateSystem)
                            .also { createdBitmaps += it }
                        val name = "$suffixName$rowNumber" + "_$columnNumber"
                        pictureRepository.saveImage(
                            getApplication(),
                            bitmap,
                            name
                        ).also { savedUris += it }
                            .let { encode(it.toString()) }
                    }
                }

                val gridId = UUID.randomUUID().toString()

                val grid = Grid(
                    gridId,
                    baseName,
                    timestamp,
                    encode(miniatureUri.toString()),
                    partEncodedUris
                )

                gridRepository.addGrid(grid)
                _croppingUiState.update { CroppingUiState.Success(gridId) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CropNGrid", "Failed to create grid", e)
                // Remove the files already written for this aborted grid
                savedUris.forEach { uri ->
                    try {
                        getApplication<Application>().contentResolver.delete(uri, null, null)
                    } catch (cleanupError: Exception) {
                        Log.e("CropNGrid", "Failed to clean up $uri", cleanupError)
                    }
                }
                _croppingUiState.update { CroppingUiState.Error }
            } finally {
                createdBitmaps.forEach { it.safeRecycle() }
            }
        }
    }

    fun resetCroppingState() {
        _croppingUiState.update { null }
    }

    fun updateGridParameters(gridParameters: GridParameters) {
        _gridParametersState.update { gridParameters }
    }
}

sealed interface CroppingUiState {
    data object Loading: CroppingUiState
    data object Error: CroppingUiState
    data class Success(val gridId: String): CroppingUiState
}