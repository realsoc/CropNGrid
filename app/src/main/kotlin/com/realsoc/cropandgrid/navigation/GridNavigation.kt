package com.realsoc.cropandgrid.navigation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.realsoc.cropandgrid.ui.screens.GridRoute
import kotlinx.coroutines.CoroutineScope

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

fun NavGraphBuilder.gridScreen(
    coroutineScope: CoroutineScope,
    onGridDeleted: () -> Unit,
    onBackClick: () -> Unit
) {
    composable(
        route = "grid/{$gridIdArg}"
    ) {
        GridRoute(coroutineScope = coroutineScope ,onGridDeleted = onGridDeleted, onBackClick = onBackClick)
    }
}
