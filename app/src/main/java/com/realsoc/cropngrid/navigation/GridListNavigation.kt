package com.realsoc.cropngrid.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.ui.screens.GridListRoute

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
        GridListRoute(onGridClicked)
    }
}
