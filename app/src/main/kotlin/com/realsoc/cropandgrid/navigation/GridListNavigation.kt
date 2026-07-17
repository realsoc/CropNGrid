package com.realsoc.cropandgrid.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.realsoc.cropandgrid.ui.screens.GridListRoute
import com.realsoc.cropandgrid.ui.theme.Lemon

const val gridListNavigationRoute = "grid_list"

fun NavController.navigateToGridList() {
    this.navigate(gridListNavigationRoute) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.gridListScreen(onGridClicked: (String) -> Unit) {
    composable(
        route = gridListNavigationRoute,
    ) {
        val color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Lemon
        GridListRoute(onGridClicked, modifier = Modifier.background(color))
    }
}
