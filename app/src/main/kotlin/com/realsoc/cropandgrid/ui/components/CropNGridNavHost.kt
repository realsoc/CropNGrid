package com.realsoc.cropandgrid.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.realsoc.cropandgrid.navigation.Transitions
import com.realsoc.cropandgrid.navigation.cropperScreen
import com.realsoc.cropandgrid.navigation.gridListNavigationRoute
import com.realsoc.cropandgrid.navigation.gridListScreen
import com.realsoc.cropandgrid.navigation.gridScreen
import com.realsoc.cropandgrid.navigation.homeNavigationRoute
import com.realsoc.cropandgrid.navigation.homeScreen
import com.realsoc.cropandgrid.navigation.infoNavigationRoute
import com.realsoc.cropandgrid.navigation.infoScreen
import com.realsoc.cropandgrid.navigation.leftToRightTransition
import com.realsoc.cropandgrid.navigation.navigateToCropper
import com.realsoc.cropandgrid.navigation.navigateToGrid
import com.realsoc.cropandgrid.navigation.navigateToGridList
import com.realsoc.cropandgrid.navigation.rightToLeftTransition
import com.realsoc.cropandgrid.ui.CropNGridAppState

@Composable
fun CropNGridNavHost(
    appState: CropNGridAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = homeNavigationRoute,
) {

    val navController = appState.navController

    val transitions = remember {
        Transitions.BASE
            .addTransition(
                homeNavigationRoute,
                gridListNavigationRoute,
                rightToLeftTransition(EaseOutCubic, 500)
            ).addTransition(
                gridListNavigationRoute,
                homeNavigationRoute,
                leftToRightTransition(EaseOutCubic, 500)
            ).addTransition(
                gridListNavigationRoute,
                infoNavigationRoute,
                leftToRightTransition(EaseOutCubic, 500)
            ).addTransition(
                infoNavigationRoute,
                gridListNavigationRoute,
                rightToLeftTransition(EaseOutCubic, 500)
            ).addTransition(
                homeNavigationRoute,
                infoNavigationRoute,
                rightToLeftTransition(EaseOutCubic, 500)
            ).addTransition(
                infoNavigationRoute,
                homeNavigationRoute,
                leftToRightTransition(EaseOutCubic, 500)
            )
    }


    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = transitions.enterTransitionChain,
        exitTransition = transitions.exitTransitionChain,
        popEnterTransition = transitions.popEnterTransitionChain,
        popExitTransition = transitions.popExitTransitionChain,
        modifier = modifier
    ) {
        homeScreen(
            onCropRequested = {
                navController.navigateToCropper(it)
            },
            onShowSnackbar = onShowSnackbar
        )
        gridListScreen(onGridClicked = {
            navController.navigateToGrid(it)
        })
        cropperScreen(
            onCropComplete = {
                navController.popBackStack(homeNavigationRoute, false)
                navController.navigateToGridList()
                navController.navigateToGrid(it)
                             },
            onBackClick = {
                navController.popBackStack()
            }
        )
        gridScreen(
            onGridDeleted = navController::popBackStack,
            onBackClick = {
                navController.popBackStack()
            }
        )
        infoScreen(
            onShowSnackbar = onShowSnackbar
        )
    }
}
