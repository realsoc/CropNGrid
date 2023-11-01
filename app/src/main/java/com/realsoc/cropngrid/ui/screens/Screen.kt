package com.realsoc.cropngrid.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.realsoc.cropngrid.MainViewModel
import com.realsoc.cropngrid.ui.screens.Screen.Home.*

sealed interface Screen {
    val route: String

    class Home(val mode: HomeMode) : Screen {
        override val route: String = "$HOME_DESTINATION/$mode"

        // Home Screen has two contents  mode start and mode crop history
        sealed class HomeMode {
            object Start: HomeMode()
            object CropHistory: HomeMode()

            companion object {
                fun parse(str: String): HomeMode? {
                    return when(str) {
                        Start.toString() -> Start
                        CropHistory.toString() -> CropHistory
                        else -> null
                    }
                }
            }
        }
    }
    class Crop(val encodedUri: String) : Screen {
        override val route: String = "$CROP_DESTINATION/$encodedUri"
    }
    class End(val loading: Boolean) : Screen {
        override val route: String = "$END_DESTINATION/${loading}"
    }

    companion object {
        private const val HOME_DESTINATION = "home"
        private const val HOME_PARAM_1 = "mode"
        private const val CROP_DESTINATION = "crop"
        private const val END_DESTINATION = "end"
        private const val CROP_PARAM_1 = "encodedUri"
        private const val END_PARAM_1 = "loading"

        val ROUTES = listOf(
            "$HOME_DESTINATION/{$HOME_PARAM_1}",
            "$CROP_DESTINATION/{$CROP_PARAM_1}",
            "$END_DESTINATION/{$END_PARAM_1}"
        )

        // transform home/totoMode in home
        private fun extractDestination(route: String): String? {
            val pattern = "\\w+"
            return Regex(pattern).find(route)?.value
        }

        fun getScreen(inputRoute: String, arguments: Bundle?): Screen {
            val desiredDestinationStr = extractDestination(inputRoute)
            val homeParameter = arguments?.getString(HOME_PARAM_1)?.let { Home.HomeMode.parse(it) }
            val cropParameter = arguments?.getString(CROP_PARAM_1)
            val endParameter = arguments?.getBoolean(END_PARAM_1)

            return when {
                desiredDestinationStr == HOME_DESTINATION && homeParameter != null -> Home(homeParameter)
                desiredDestinationStr == CROP_DESTINATION && cropParameter != null -> Crop(cropParameter)
                desiredDestinationStr == END_DESTINATION && endParameter != null -> End(endParameter)
                else -> {
                    // Log && error
                    Home(HomeMode.Start)
                }
            }
        }

        @Composable
        fun Content(viewModel: MainViewModel, screen: Screen) {
            when(screen) {
                is Home -> {
                    when(screen.mode) {
                        HomeMode.Start -> HomeContent(viewModel = viewModel)
                        HomeMode.CropHistory -> CropHistoryContent()
                    }
                }
                is Crop -> CropContent(viewModel)
                is End -> EndContent(viewModel)
            }
        }

        @Composable
        fun TopBar(viewModel: MainViewModel, screen: Screen) {
            when(screen) {
                is Home -> HomeAppBar(viewModel = viewModel)
                is Crop -> _TopBar(title = "Cropping", backEnabled = true, viewModel::onBackClicked)
                is End -> _TopBar(title = "Ending", backEnabled = false, viewModel::onBackClicked)
            }
        }

        @Composable
        fun BottomBar(viewModel: MainViewModel, screen: Screen) {
            when(screen) {
                is Home -> HomeBottomBar(viewModel = viewModel)
                is Crop -> CropBottomBar(viewModel = viewModel)
                is End -> EndBottomBar(viewModel = viewModel)
            }
        }

        @SuppressLint("ComposableNaming")
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        private fun _TopBar(title: String, backEnabled: Boolean, onBackClicked: () -> Unit) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = { Text(title) },
                navigationIcon = {
                    if (backEnabled) {
                        IconButton(onClick = onBackClicked) {
                            Icon(Icons.Default.ArrowBack, "Back button")
                        }
                    }
                }
            )
        }
    }
}