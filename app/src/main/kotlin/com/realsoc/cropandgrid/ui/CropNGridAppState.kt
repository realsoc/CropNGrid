package com.realsoc.cropandgrid.ui

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
import com.realsoc.cropandgrid.navigation.TopLevelDestination
import com.realsoc.cropandgrid.navigation.TopLevelDestination.GRID_LIST
import com.realsoc.cropandgrid.navigation.TopLevelDestination.HOME
import com.realsoc.cropandgrid.navigation.TopLevelDestination.INFO
import com.realsoc.cropandgrid.navigation.TopLevelDestination.values
import com.realsoc.cropandgrid.navigation.gridListNavigationRoute
import com.realsoc.cropandgrid.navigation.homeNavigationRoute
import com.realsoc.cropandgrid.navigation.navigateToGridList
import com.realsoc.cropandgrid.navigation.navigateToHome
import com.realsoc.cropandgrid.navigation.navigateToInfo
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