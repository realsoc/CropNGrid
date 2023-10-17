package com.realsoc.image_cropper.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.realsoc.image_cropper.BitmapTransformTest
import com.realsoc.image_cropper.MainViewModel
import com.realsoc.image_cropper.UiState
import com.realsoc.image_cropper.encode

@Composable
fun MainScreen(fetchPicture: () -> Unit) {
    val viewModel: MainViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen (onStartButtonClicked = fetchPicture) }
        composable("crop/{encodedUri}") { CropScreen(encodedUri = it.arguments?.getString("encodedUri"),
            plusClicked = fetchPicture) }
        composable("crop") { CropScreen(plusClicked = fetchPicture) }
        composable("end") { EndScreen() }
        composable("test") { BitmapTransformTest() }
    }

    LaunchedEffect(Unit) {
        viewModel.uiState.collect() {
            when(it) {
                is UiState.Started -> navController.navigate("crop")//navController.navigate("home")
                is UiState.CropRequest -> navController.navigate("crop/${ it.uri.encode() }")
                is UiState.Success -> navController.navigate("end")
            }
        }
    }
}