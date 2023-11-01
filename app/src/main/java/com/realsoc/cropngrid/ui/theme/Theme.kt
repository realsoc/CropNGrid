package com.realsoc.cropngrid.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LimonSiciliano,
    onPrimary = Black,
    background = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = White,
    background = LimonSiciliano,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    Secondary =
    Tertiary =
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val LightColors = lightColorScheme(
    primary = com.example.compose.md_theme_light_primary,
    onPrimary = com.example.compose.md_theme_light_onPrimary,
    primaryContainer = com.example.compose.md_theme_light_primaryContainer,
    onPrimaryContainer = com.example.compose.md_theme_light_onPrimaryContainer,
    secondary = com.example.compose.md_theme_light_secondary,
    onSecondary = com.example.compose.md_theme_light_onSecondary,
    secondaryContainer = com.example.compose.md_theme_light_secondaryContainer,
    onSecondaryContainer = com.example.compose.md_theme_light_onSecondaryContainer,
    tertiary = com.example.compose.md_theme_light_tertiary,
    onTertiary = com.example.compose.md_theme_light_onTertiary,
    tertiaryContainer = com.example.compose.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.example.compose.md_theme_light_onTertiaryContainer,
    error = com.example.compose.md_theme_light_error,
    errorContainer = com.example.compose.md_theme_light_errorContainer,
    onError = com.example.compose.md_theme_light_onError,
    onErrorContainer = com.example.compose.md_theme_light_onErrorContainer,
    background = com.example.compose.md_theme_light_background,
    onBackground = com.example.compose.md_theme_light_onBackground,
    surface = com.example.compose.md_theme_light_surface,
    onSurface = com.example.compose.md_theme_light_onSurface,
    surfaceVariant = com.example.compose.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.example.compose.md_theme_light_onSurfaceVariant,
    outline = com.example.compose.md_theme_light_outline,
    inverseOnSurface = com.example.compose.md_theme_light_inverseOnSurface,
    inverseSurface = com.example.compose.md_theme_light_inverseSurface,
    inversePrimary = com.example.compose.md_theme_light_inversePrimary,
    surfaceTint = com.example.compose.md_theme_light_surfaceTint,
    outlineVariant = com.example.compose.md_theme_light_outlineVariant,
    scrim = com.example.compose.md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = com.example.compose.md_theme_dark_primary,
    onPrimary = com.example.compose.md_theme_dark_onPrimary,
    primaryContainer = com.example.compose.md_theme_dark_primaryContainer,
    onPrimaryContainer = com.example.compose.md_theme_dark_onPrimaryContainer,
    secondary = com.example.compose.md_theme_dark_secondary,
    onSecondary = com.example.compose.md_theme_dark_onSecondary,
    secondaryContainer = com.example.compose.md_theme_dark_secondaryContainer,
    onSecondaryContainer = com.example.compose.md_theme_dark_onSecondaryContainer,
    tertiary = com.example.compose.md_theme_dark_tertiary,
    onTertiary = com.example.compose.md_theme_dark_onTertiary,
    tertiaryContainer = com.example.compose.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.example.compose.md_theme_dark_onTertiaryContainer,
    error = com.example.compose.md_theme_dark_error,
    errorContainer = com.example.compose.md_theme_dark_errorContainer,
    onError = com.example.compose.md_theme_dark_onError,
    onErrorContainer = com.example.compose.md_theme_dark_onErrorContainer,
    background = com.example.compose.md_theme_dark_background,
    onBackground = com.example.compose.md_theme_dark_onBackground,
    surface = com.example.compose.md_theme_dark_surface,
    onSurface = com.example.compose.md_theme_dark_onSurface,
    surfaceVariant = com.example.compose.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.example.compose.md_theme_dark_onSurfaceVariant,
    outline = com.example.compose.md_theme_dark_outline,
    inverseOnSurface = com.example.compose.md_theme_dark_inverseOnSurface,
    inverseSurface = com.example.compose.md_theme_dark_inverseSurface,
    inversePrimary = com.example.compose.md_theme_dark_inversePrimary,
    surfaceTint = com.example.compose.md_theme_dark_surfaceTint,
    outlineVariant = com.example.compose.md_theme_dark_outlineVariant,
    scrim = com.example.compose.md_theme_dark_scrim,
)


@Composable
fun CropNGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}