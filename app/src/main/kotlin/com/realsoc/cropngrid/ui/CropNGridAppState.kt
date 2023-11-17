package com.realsoc.cropngrid.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.realsoc.cropngrid.navigation.TopLevelDestination
import com.realsoc.cropngrid.navigation.TopLevelDestination.GRID_LIST
import com.realsoc.cropngrid.navigation.TopLevelDestination.HOME
import com.realsoc.cropngrid.navigation.TopLevelDestination.INFO
import com.realsoc.cropngrid.navigation.TopLevelDestination.values
import com.realsoc.cropngrid.navigation.gridListNavigationRoute
import com.realsoc.cropngrid.navigation.homeNavigationRoute
import com.realsoc.cropngrid.navigation.navigateToGridList
import com.realsoc.cropngrid.navigation.navigateToHome
import com.realsoc.cropngrid.navigation.navigateToInfo
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberCropNGridAppState(
    windowSizeClass: WindowSizeClass,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): CropNGridAppState {
    return remember(
        navController,
        coroutineScope,
        windowSizeClass,
    ) {
        CropNGridAppState(
            navController,
            coroutineScope,
            windowSizeClass,
        )
    }
}

@Stable
class CropNGridAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            homeNavigationRoute -> HOME
            gridListNavigationRoute -> GRID_LIST
            else -> null
        }

    val shouldShowBottomControl: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowLateralControl: Boolean
        get() = !shouldShowBottomControl

    val shouldShowBottomBar: Boolean
        @Composable get() = shouldShowBottomControl && currentDestination.isTopLevelDestination()

    val shouldShowNavRail: Boolean
        @Composable get() = shouldShowLateralControl && currentDestination.isTopLevelDestination()


    val topLevelDestinations: List<TopLevelDestination> = values().asList()

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
            when (topLevelDestination) {
                HOME -> navController.navigateToHome()
                INFO -> navController.navigateToInfo()
                GRID_LIST -> navController.navigateToGridList()
            }

    }
}

@Stable
fun NavDestination?.isTopLevelDestination() = values()
    .any { topLevelDestination -> this?.route?.contains(topLevelDestination.name, true) ?: false }