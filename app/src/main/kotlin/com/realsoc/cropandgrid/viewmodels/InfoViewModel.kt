package com.realsoc.cropandgrid.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsoc.cropandgrid.data.PreferencesRepository
import com.realsoc.cropandgrid.Result
import com.realsoc.cropandgrid.analytics.AnalyticsHelper
import com.realsoc.cropandgrid.analytics.logLogGranted
import com.realsoc.cropandgrid.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val analyticsHelper: AnalyticsHelper
): ViewModel() {
    fun onLogGranted(logGranted: Boolean) {
        viewModelScope.launch {
            analyticsHelper.logLogGranted(logGranted)
            try {
                preferencesRepository.setLogGranted(logGranted)
            } catch (e: IOException) {
                Log.e("CropNGrid", "Failed to persist analytics preference", e)
            }
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