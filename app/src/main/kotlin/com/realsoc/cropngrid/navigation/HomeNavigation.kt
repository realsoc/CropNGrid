package com.realsoc.cropngrid.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.ui.screens.HomeRoute
import com.realsoc.cropngrid.ui.theme.Lemon

const val homeNavigationRoute = "home"

fun NavController.navigateToHome() {
    this.navigate(homeNavigationRoute) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.homeScreen(
    onCropRequested: (Uri) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable(
        route = homeNavigationRoute
    ) {
        val color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Lemon
        HomeRoute(
            onShowSnackbar,
            onCropRequested,
            modifier = Modifier.background(color)
        )
    }
}
