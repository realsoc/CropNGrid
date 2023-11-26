package com.realsoc.cropngrid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsoc.cropngrid.Result
import com.realsoc.cropngrid.analytics.AnalyticsHelper
import com.realsoc.cropngrid.analytics.logLogGranted
import com.realsoc.cropngrid.asResult
import com.realsoc.cropngrid.data.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InfoViewModel (
    private val preferencesRepository: PreferencesRepository,
    private val analyticsHelper: AnalyticsHelper
): ViewModel() {
    fun onLogGranted(logGranted: Boolean) {
        viewModelScope.launch {
            analyticsHelper.logLogGranted(logGranted)
            preferencesRepository.setLogGranted(logGranted)
        }
    }

    val infoUiState: StateFlow<InfoUiState> = preferencesRepository.getLogGranted()
        .asResult()
        .map { result -> when(result) {
            is Result.Success -> {
                InfoUiState.Success(result.data)
            }
            is Result.Error -> {
                InfoUiState.Error
            }
            Result.Loading -> InfoUiState.Loading
        }}.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InfoUiState.Loading,
        )

}

sealed interface InfoUiState {
    data class Success(val logGranted: Boolean) : InfoUiState
    data object Loading : InfoUiState
    data object Error : InfoUiState
}