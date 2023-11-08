package com.realsoc.cropngrid.navigation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.ui.screens.GridRoute

@VisibleForTesting
internal const val gridIdArg = "gridId"
internal class GridArgs(val gridId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[gridIdArg]) as String)
}

fun NavController.navigateToGrid(gridId: String) {
    this.navigate("grid/$gridId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.gridScreen(onGridDeleted: () -> Unit, onBackClick: () -> Unit) {
    composable(
        route = "grid/{$gridIdArg}",
        enterTransition = null,
        exitTransition = null,
        popEnterTransition = null,
        popExitTransition = null
    ) {
        GridRoute(onGridDeleted = onGridDeleted, onBackClick = onBackClick)
    }
}
