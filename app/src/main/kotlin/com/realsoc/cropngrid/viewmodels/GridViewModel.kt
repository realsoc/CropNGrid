package com.realsoc.cropngrid.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.realsoc.cropngrid.Result
import com.realsoc.cropngrid.asResult
import com.realsoc.cropngrid.data.GridRepository
import com.realsoc.cropngrid.data.PictureRepository
import com.realsoc.cropngrid.decode
import com.realsoc.cropngrid.getBitmap
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.navigation.GridArgs
import com.realsoc.cropngrid.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class GridViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val gridRepository: GridRepository,
    private val pictureRepository: PictureRepository
): AndroidViewModel(application) {
    suspend fun saveGrid(grid: Grid) {
        withContext(Dispatchers.IO) {
            grid.parts.mapIndexed { rowCount, row ->
                row.mapIndexed { columnCount, part ->
                    val bitmap = getApplication<Application>().contentResolver.getBitmap(decode(part).toUri())
                    pictureRepository.saveImage(
                        getApplication(),
                        bitmap,
                        grid.name + ".$rowCount.$columnCount",
                        true
                    )
                }
            }
        }
    }

    suspend fun deleteGrid(grid: Grid) {
        withContext(Dispatchers.IO) {
            gridRepository.deleteGrid(grid)
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