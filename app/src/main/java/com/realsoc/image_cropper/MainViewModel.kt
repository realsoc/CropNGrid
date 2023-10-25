package com.realsoc.image_cropper

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.realsoc.image_cropper.ui.models.CoordinateSystem
import com.realsoc.image_cropper.ui.scale
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel(application: Application):
AndroidViewModel(application) {

    private val pictureRepository: PictureRepository = PictureRepositoryImpl()

    private val snackbarManager = SnackbarManager

    private val _uiState = MutableStateFlow<Screen>(Screen.Started())
    val uiState: StateFlow<Screen> = _uiState.asStateFlow()


    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            println("Updating to cropper ui debug")
            _uiState.value = Screen.Cropper(uri)
        }
    }

    fun cropImageInParts(
        source: Bitmap,
        pParts: List<Rect>,
        pCoordinateSystem: CoordinateSystem,
        baseName: String
    ) {
        viewModelScope.launch {
            _uiState.value = Screen.End(true)

            val job = launch { delay(500) }

            var coordinateSystem = pCoordinateSystem
            var parts = pParts
            val time = System.currentTimeMillis()

            if (pCoordinateSystem.transformation.scale != 0f && pCoordinateSystem.transformation.scale < 1f) {
                val newScale = 1 / pCoordinateSystem.transformation.scale
                coordinateSystem = pCoordinateSystem.withTransformation(pCoordinateSystem.transformation.scale(newScale))
                parts = parts.map { it.scale(newScale, pCoordinateSystem.pivot) }
            }

            parts.map {
                async {
                    createBitmapOfArea(source, it, coordinateSystem)
                }
            }.awaitAll().forEachIndexed { index, bitmap ->
                try {
                    pictureRepository.saveImage(
                        getApplication<Application>().contentResolver,
                        bitmap,
                        baseName + "_" + time + "_" + index
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Log
                    snackbarManager.showState(androidx.compose.ui.R.string.default_error_message)
                } finally {
                    bitmap.safeRecycle()
                }
            }

            // Todo : remove and have a good animation
            job.join()

            _uiState.value = Screen.End(false)
        }
    }

    fun closeCrop() {
        viewModelScope.launch {
            println("Updating started debug")

            _uiState.value = Screen.Started()
        }
    }

    fun storageRecused(startPermissionIntent: () -> Unit) {
        viewModelScope.launch {
            snackbarManager.showState(
                messageTextId = R.string.permission_needed,
                overrideSameTextId = true,
                action = Action(textId = R.string.grant, onClick = startPermissionIntent))
        }
    }

    fun terminate() {
        viewModelScope.launch {
            _uiState.value = Screen.Started()
        }
    }


}

sealed interface Screen {
    class Started(val showOnboarding: Boolean = false): Screen
    class Cropper(val uri: Uri): Screen
    class End(val loading: Boolean): Screen
}