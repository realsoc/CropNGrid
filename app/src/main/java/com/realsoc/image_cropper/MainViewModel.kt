package com.realsoc.image_cropper

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Started)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Create a consume once error stream


    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.CropRequest(uri)
        }
    }

    fun closeCrop() {
        viewModelScope.launch {
            _uiState.value = UiState.Started
        }
    }


}

sealed class UiState {
    //object Starting: UiState()
    object Started: UiState()
    data class CropRequest(val uri: Uri): UiState()
    data class Success(val destinations: List<String>): UiState()
}