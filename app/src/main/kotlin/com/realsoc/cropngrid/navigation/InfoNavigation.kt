package com.realsoc.cropngrid.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.ui.screens.InfoRoute
import com.realsoc.cropngrid.ui.theme.Blue
import kotlinx.coroutines.CoroutineScope

const val infoNavigationRoute = "info"

fun NavController.navigateToInfo() {
    this.navigate(infoNavigationRoute) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.infoScreen(
    coroutineScope: CoroutineScope,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable(
        route = infoNavigationRoute
    ) {
        InfoRoute(
            coroutineScope = coroutineScope,
            modifier = Modifier.background(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Blue),
            onShowSnackbar = onShowSnackbar
        )
    }
}
