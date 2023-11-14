package com.realsoc.cropngrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.realsoc.cropngrid.ui.CropNGridApp
import com.realsoc.cropngrid.ui.components.Background
import com.realsoc.cropngrid.ui.theme.CropNGridTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        setContent {
            CropNGridTheme(dynamicColor = false) {
                Background {
                    ChangeSystemBarsTheme(!isSystemInDarkTheme())
                    CropNGridApp(
                        windowSizeClass = calculateWindowSizeClass(this)
                    )
                }

            }
        }
    }

    @Composable
    private fun ChangeSystemBarsTheme(lightTheme: Boolean) {
        val topBarColor = MaterialTheme.colorScheme.surface.toArgb()
        val bottomBarColor = MaterialTheme.colorScheme.surface.toArgb()
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