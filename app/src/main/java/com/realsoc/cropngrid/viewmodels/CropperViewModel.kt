package com.realsoc.cropngrid.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.realsoc.cropngrid.GridRepository
import com.realsoc.cropngrid.PictureRepository
import com.realsoc.cropngrid.createBitmapOfArea
import com.realsoc.cropngrid.encode
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.navigation.CropperArgs
import com.realsoc.cropngrid.safeRecycle
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import com.realsoc.cropngrid.ui.scale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val croppingUiState: StateFlow<CroppingUiState?> = _croppingUiState

    val pictureUri: Uri = cropArgs.uri

    suspend fun makeGrid(
        source: Bitmap,
        miniatureArea: Rect,
        pParts: List<List<Rect>>,
        pCoordinateSystem: CoordinateSystem,
        baseName: String?
    ) {
        println("Starting cropping")
        viewModelScope.launch {
            _croppingUiState.update { CroppingUiState.Loading }

            withContext(Dispatchers.IO) {
                try {
                    var coordinateSystem = pCoordinateSystem
                    var parts = pParts
                    val timestamp = System.currentTimeMillis()

                    @Suppress("NAME_SHADOWING") val baseName = (baseName ?: "UNKNOWN") + "_" + timestamp

                    val suffixName = baseName + "_"

                    val miniatureBitmap = createBitmapOfArea(source, miniatureArea, coordinateSystem)
                    val miniatureUri = checkNotNull(
                        pictureRepository.saveImage(
                            getApplication<Application>().contentResolver,
                            miniatureBitmap,
                            suffixName + "MINIATURE"
                        )
                    )

                    if (pCoordinateSystem.transformation.scale != 0f && pCoordinateSystem.transformation.scale < 1f) {
                        val newScale = 1 / pCoordinateSystem.transformation.scale
                        coordinateSystem =
                            pCoordinateSystem.withTransformation(pCoordinateSystem.transformation.scale(newScale))
                        parts = parts.map { row -> row.map { it.scale(newScale, pCoordinateSystem.pivot) } }
                    }

                    val partBitmap = parts.map { row ->
                        row.map { part ->
                            createBitmapOfArea(source, part, coordinateSystem)
                        }
                    }
                    val partEncodedUris = partBitmap.mapIndexed { rowNumber, row ->
                        row.mapIndexed { columnNumber, bitmap ->
                            val name = "$suffixName$rowNumber:$columnNumber"
                            checkNotNull(
                                pictureRepository.saveImage(
                                    getApplication<Application>().contentResolver,
                                    bitmap,
                                    name
                                )
                            ).let { encode(it.toString()) }
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

                    println("Cropped grid : $grid")

                    gridRepository.addGrid(grid)
                    _croppingUiState.update { CroppingUiState.Success(gridId) }
                    miniatureBitmap.safeRecycle()
                    partBitmap.map { it.map { it.safeRecycle() } }
                } catch (e: Exception) {
                    println("damn an error")
                    println(e)
                    _croppingUiState.update { CroppingUiState.Error }
                }
            }
        }
    }
}

sealed interface CroppingUiState {
    data object Loading: CroppingUiState
    data object Error: CroppingUiState
    data class Success(val gridId: String): CroppingUiState
}