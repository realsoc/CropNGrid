package com.realsoc.cropngrid

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.realsoc.cropngrid.ui.components.navigateTo
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import com.realsoc.cropngrid.ui.scale
import com.realsoc.cropngrid.ui.screens.Screen
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface AndroidActions {
    fun pickPhotoRequest()
}
class MainViewModel(
    application: Application,
    private val navController: NavHostController,
    private val androidActions: AndroidActions,
    private val pictureRepository: PictureRepository = PictureRepositoryImpl()
) :
    AndroidViewModel(application) {

    private val _onDestinationChangedListener = NavController.OnDestinationChangedListener { _, destination,
                                                                                             arguments ->
        destination.route?.let { _uiState.value = Screen.getScreen(it, arguments) }
    }

    init {
        navController.addOnDestinationChangedListener(_onDestinationChangedListener)
    }

    override fun onCleared() {
        super.onCleared()
        navController.removeOnDestinationChangedListener(_onDestinationChangedListener)
    }

    private val snackbarManager = SnackbarManager

    private val _uiState = MutableStateFlow<Screen>(Screen.Home(Screen.Home.HomeMode.Start))
    val uiState: StateFlow<Screen> = _uiState.asStateFlow()

    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            navController.navigateTo(Screen.Crop(uri.encode()))
        }
    }

    fun cropImageInParts(
        source: Bitmap,
        pParts: List<Rect>,
        pCoordinateSystem: CoordinateSystem,
        baseName: String
    ) {
        viewModelScope.launch {
            navController.navigateTo(Screen.End(true))

            var coordinateSystem = pCoordinateSystem
            var parts = pParts
            val time = System.currentTimeMillis()

            if (pCoordinateSystem.transformation.scale != 0f && pCoordinateSystem.transformation.scale < 1f) {
                val newScale = 1 / pCoordinateSystem.transformation.scale
                coordinateSystem =
                    pCoordinateSystem.withTransformation(pCoordinateSystem.transformation.scale(newScale))
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
                    // Todo : Log
                    snackbarManager.showState(androidx.compose.ui.R.string.default_error_message)
                } finally {
                    bitmap.safeRecycle()
                }
            }
            navController.navigateTo(Screen.End(false))
        }
    }

    fun storageRecused(startPermissionIntent: () -> Unit) {
        viewModelScope.launch {
            snackbarManager.showState(
                messageTextId = R.string.permission_needed,
                overrideSameTextId = true,
                action = Action(textId = R.string.grant, onClick = startPermissionIntent)
            )
        }
    }

    fun terminate() {
        viewModelScope.launch {
            //navController.navigateTo(Screen.Home)
        }
    }

    fun startButtonClickedHome() {
        viewModelScope.launch {
            androidActions.pickPhotoRequest()
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            navController.popBackStack()
        }
    }

    fun homeClicked() {
        TODO("Not yet implemented")
    }

    fun cropListClicked() {
        viewModelScope.launch {
            navController.navigateTo(Screen.Home(Screen.Home.HomeMode.CropHistory))
        }
    }

    class Factory(private val app: Application, private val navHostController: NavHostController,
                  private val androidActions:
                  AndroidActions) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(app, navHostController, androidActions) as T
        }
    }
}

