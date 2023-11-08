package com.realsoc.cropngrid.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.realsoc.cropngrid.ui.screens.HomeRoute

const val homeNavigationRoute = "home"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onCropRequested: (Uri) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable(
        route = homeNavigationRoute,
        enterTransition = null,
        exitTransition = null,
        popEnterTransition = null,
        popExitTransition = null
        /*enterTransition = {
            when(initialState.destination.route) {
                gridListNavigationRoute ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        exitTransition = {
            when(targetState.destination.route) {
                gridListNavigationRoute ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },*/
    ) {
        HomeRoute(onShowSnackbar, onCropRequested)
    }
}
