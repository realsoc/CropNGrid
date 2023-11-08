package com.realsoc.cropngrid.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import com.realsoc.cropngrid.navigation.Transitions
import com.realsoc.cropngrid.navigation.cropperScreen
import com.realsoc.cropngrid.navigation.gridListNavigationRoute
import com.realsoc.cropngrid.navigation.gridListScreen
import com.realsoc.cropngrid.navigation.gridScreen
import com.realsoc.cropngrid.navigation.homeNavigationRoute
import com.realsoc.cropngrid.navigation.homeScreen
import com.realsoc.cropngrid.navigation.leftToRightTransition
import com.realsoc.cropngrid.navigation.navigateToCropper
import com.realsoc.cropngrid.navigation.navigateToGrid
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
            onCropRequested = navController::navigateToCropper,
            onShowSnackbar = onShowSnackbar
        )
        gridListScreen(onGridClicked = navController::navigateToGrid)
        cropperScreen(
            onCropComplete = navController::navigateToGrid,
            coroutineScope = appState.coroutineScope,
            onBackClick = navController::popBackStack
        )
        gridScreen(onGridDeleted = navController::popBackStack, onBackClick = navController::popBackStack)
    }
}




private const val TIME_DURATION = 300

val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(durationMillis = TIME_DURATION, easing = LinearOutSlowInEasing)
    )
}

val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { -it / 3 },
        animationSpec = tween(durationMillis = TIME_DURATION, easing = LinearOutSlowInEasing)
    )
}

val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec = tween(durationMillis = TIME_DURATION, easing = LinearOutSlowInEasing)
    )
}

val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(durationMillis = TIME_DURATION, easing = LinearOutSlowInEasing)
    )
}