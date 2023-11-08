package com.realsoc.cropngrid.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.ui.screens.GridListRoute

const val gridListNavigationRoute = "grid_list"

fun NavController.navigateToGridList(navOptions: NavOptions? = null) {
    this.navigate(gridListNavigationRoute, navOptions)
}

fun NavGraphBuilder.gridListScreen(onGridClicked: (String) -> Unit) {
    composable(
        route = gridListNavigationRoute,
        /*enterTransition = {
            when(initialState.destination.route) {
                gridListNavigationRoute ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        exitTransition = {
            when(targetState.destination.route) {
                gridListNavigationRoute ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },*/
    ) {
        GridListRoute(onGridClicked)
    }
}
