package com.realsoc.cropngrid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsoc.cropngrid.data.GridRepository
import com.realsoc.cropngrid.Result
import com.realsoc.cropngrid.asResult
import com.realsoc.cropngrid.models.Grid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GridListViewModel @Inject constructor(
    gridRepository: GridRepository
): ViewModel() {

    val gridListUiState: StateFlow<GridListUiState> = gridRepository.getGrids()
        .asResult()
        .map { result -> when(result) {
            is Result.Error -> {
                GridListUiState.Error
            }
            Result.Loading -> GridListUiState.Loading
            is Result.Success -> GridListUiState.Success(result.data.sortedByDescending { it.utcDate })
        }}.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GridListUiState.Loading,
        )
}

sealed interface GridListUiState {
    data class Success(val gridList: List<Grid>) : GridListUiState
    object Loading : GridListUiState
    object Error : GridListUiState

}