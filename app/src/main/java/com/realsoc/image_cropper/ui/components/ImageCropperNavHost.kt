package com.realsoc.image_cropper.ui.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.realsoc.image_cropper.MainViewModel
import com.realsoc.image_cropper.Screen
import com.realsoc.image_cropper.encode
import com.realsoc.image_cropper.ui.screens.CropScreen
import com.realsoc.image_cropper.ui.screens.EndScreen
import com.realsoc.image_cropper.ui.screens.HomeScreen

@Composable
fun ImageCropperNavHost(navController: NavHostController, fetchPicture: () -> Unit, onCanPop: (Boolean) -> Unit) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val viewModel: MainViewModel = viewModel()


    NavHost(navController = navController, startDestination = "home") {
        composable(viewModelStoreOwner, "home") {
            onCanPop(false)
            HomeScreen(onStartButtonClicked = fetchPicture)
        }
        composable(viewModelStoreOwner, "crop/{encodedUri}") {
            onCanPop(true)
            CropScreen(
                encodedUri = it.arguments?.getString("encodedUri")!!,
                plusClicked = fetchPicture
            )
        }
        composable(viewModelStoreOwner, "end") {
            onCanPop(true)
            EndScreen()
        }
        composable(viewModelStoreOwner, "end/{loading}") {
            onCanPop(true)
            EndScreen(loading = it.arguments?.getString("loading")?.toBoolean()!!)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiState.collect {
            when (it) {
                is Screen.Started -> navController.navigate("home") {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
                is Screen.Cropper -> navController.navigate("crop/${it.uri.encode()}") {
                    launchSingleTop = true
                }
                is Screen.End -> navController.navigate("end/${it.loading}") {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
        }
    }
}

fun NavGraphBuilder.composable(
    viewModelStoreOwner: ViewModelStoreOwner,
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(route) {
        CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
            content(this, it)
        }
    }
}