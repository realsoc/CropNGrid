package com.realsoc.cropngrid.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.realsoc.cropngrid.data.GridRepository
import com.realsoc.cropngrid.data.PictureRepository
import com.realsoc.cropngrid.Result
import com.realsoc.cropngrid.asResult
import com.realsoc.cropngrid.decode
import com.realsoc.cropngrid.getBitmap
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.navigation.GridArgs
import com.realsoc.cropngrid.safeRecycle
import com.realsoc.cropngrid.toUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val gridRepository: GridRepository,
    private val pictureRepository: PictureRepository
): AndroidViewModel(application) {
    /**
     * Saves all grid parts to public storage. Returns true if every part was saved.
     */
    suspend fun saveGrid(grid: Grid): Boolean = withContext(Dispatchers.IO) {
        try {
            grid.parts.forEachIndexed { rowCount, row ->
                row.forEachIndexed { columnCount, part ->
                    val bitmap = getApplication<Application>().contentResolver.getBitmap(decode(part).toUri())
                    try {
                        pictureRepository.saveImage(
                            getApplication(),
                            bitmap,
                            grid.name + ".$rowCount.$columnCount",
                            true
                        )
                    } finally {
                        bitmap.safeRecycle()
                    }
                }
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("CropNGrid", "Failed to save grid parts", e)
            false
        }
    }

    /**
     * Deletes the grid and its underlying image files. Returns true on success.
     */
    suspend fun deleteGrid(grid: Grid): Boolean = withContext(Dispatchers.IO) {
        try {
            gridRepository.deleteGrid(grid)
            // Best-effort cleanup: the images are useless without the grid row
            val resolver = getApplication<Application>().contentResolver
            (grid.parts.flatten() + grid.miniatureUriEncoded).forEach { encodedUri ->
                try {
                    resolver.delete(decode(encodedUri).toUri(), null, null)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("CropNGrid", "Failed to delete grid image $encodedUri", e)
                }
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("CropNGrid", "Failed to delete grid ${grid.id}", e)
            false
        }
    }

    private val gridArgs: GridArgs = GridArgs(savedStateHandle)

    val gridUiState: StateFlow<GridUiState> = gridRepository.getGrid(id = gridArgs.gridId)
        .filterNotNull()
        .asResult()
        .map { result -> when(result) {
            is Result.Success -> {

                GridUiState.Success(result.data)
            }
            is Result.Error -> {
                GridUiState.Error
            }
            Result.Loading -> GridUiState.Loading
        }}.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GridUiState.Loading,
        )


}

sealed interface GridUiState {
    data class Success(val grid: Grid) : GridUiState
    data object Loading : GridUiState
    data object Error : GridUiState
}