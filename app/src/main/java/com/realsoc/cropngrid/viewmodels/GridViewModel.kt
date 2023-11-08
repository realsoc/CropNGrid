package com.realsoc.cropngrid.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsoc.cropngrid.GridRepository
import com.realsoc.cropngrid.Result
import com.realsoc.cropngrid.asResult
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.navigation.GridArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gridRepository: GridRepository
): ViewModel() {
    fun deleteGrid(grid: Grid) {
        viewModelScope.launch {
            gridRepository.deleteGrid(grid)
        }
    }

    private val gridArgs: GridArgs = GridArgs(savedStateHandle)

    val gridUiState: StateFlow<GridUiState> = gridRepository.getGrid(id = gridArgs.gridId)
        .asResult()
        .map { result -> when(result) {
            is Result.Success -> GridUiState.Success(result.data)
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