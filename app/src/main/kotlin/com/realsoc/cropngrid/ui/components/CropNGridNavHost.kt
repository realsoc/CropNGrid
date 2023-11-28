package com.realsoc.cropngrid.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.realsoc.cropngrid.navigation.Transitions
import com.realsoc.cropngrid.navigation.cropperScreen
import com.realsoc.cropngrid.navigation.gridListNavigationRoute
import com.realsoc.cropngrid.navigation.gridListScreen
import com.realsoc.cropngrid.navigation.gridScreen
import com.realsoc.cropngrid.navigation.homeNavigationRoute
import com.realsoc.cropngrid.navigation.homeScreen
import com.realsoc.cropngrid.navigation.infoNavigationRoute
import com.realsoc.cropngrid.navigation.infoScreen
import com.realsoc.cropngrid.navigation.leftToRightTransition
import com.realsoc.cropngrid.navigation.navigateToCropper
import com.realsoc.cropngrid.navigation.navigateToGrid
import com.realsoc.cropngrid.navigation.navigateToGridList
import com.realsoc.cropngrid.navigation.rightToLeftTransition
import com.realsoc.cropngrid.ui.CropNGridAppState

@Composable
fun CropNGridNavHost(
    appState: CropNGridAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = homeNavigationRoute,
) {

    val navController = appState.navController

    val transitions = Transitions.BASE
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
            coroutineScope = appState.coroutineScope,
            onBackClick = {
                navController.popBackStack()
            }
        )
        gridScreen(
            coroutineScope = appState.coroutineScope,
            onGridDeleted = navController::popBackStack,
            onBackClick = {
                navController.popBackStack()
            }
        )
        infoScreen(
            coroutineScope = appState.coroutineScope,
            onShowSnackbar = onShowSnackbar
        )
    }
}
