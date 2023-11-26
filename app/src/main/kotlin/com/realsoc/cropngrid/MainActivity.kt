package com.realsoc.cropngrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.realsoc.cropngrid.analytics.AnalyticsHelper
import com.realsoc.cropngrid.analytics.LocalAnalyticsHelper
import com.realsoc.cropngrid.ui.CropNGridApp
import com.realsoc.cropngrid.ui.components.Background
import com.realsoc.cropngrid.ui.theme.CropNGridTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val analyticsHelper by inject<AnalyticsHelper>()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
                CropNGridTheme() {
                    Background {
                        ChangeSystemBarsTheme(!isSystemInDarkTheme())
                        CropNGridApp(
                            windowSizeClass = calculateWindowSizeClass(this)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ChangeSystemBarsTheme(lightTheme: Boolean) {
        val topBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp).toArgb()
        val bottomBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp).toArgb()
        LaunchedEffect(lightTheme) {
            if (lightTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        topBarColor, topBarColor,
                    ),
                    navigationBarStyle = SystemBarStyle.light(
                        bottomBarColor, bottomBarColor,
                    ),
                )
            } else {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(
                        topBarColor,
                    ),
                    navigationBarStyle = SystemBarStyle.dark(
                        bottomBarColor,
                    ),
                )
            }
        }
    }
}