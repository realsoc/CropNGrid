package com.realsoc.cropngrid.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.MainViewModel
import com.realsoc.cropngrid.ui.screens.Screen
import com.realsoc.cropngrid.ui.screens.Screen.Companion.getScreen

@Composable
fun CropNGridNavHost(navController: NavHostController, viewModel: MainViewModel) {

    NavHost(navController = navController, startDestination = "home/${Screen.Home.HomeMode.Start}") {
        for (route in Screen.ROUTES) {
            composable(route) {
                Screen.Content(viewModel = viewModel, getScreen(route, it.arguments))
            }
        }
    }
}
fun NavHostController.navigateTo(screen: Screen, popUp: Boolean = false) {
    when (screen) {
        is Screen.Home -> navigate(screen.route) {
            if (popUp) popUpTo(graph.findStartDestination().id)
            launchSingleTop = true
        }
        is Screen.Crop -> navigate(screen.route) {
            if (popUp) popUpTo(graph.findStartDestination().id)
            launchSingleTop = true
        }
        is Screen.End -> navigate(screen.route) {
            if (popUp) popUpTo(graph.findStartDestination().id)
            launchSingleTop = true
        }
    }
}
