package com.realsoc.cropngrid.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    // Provide a default AnalyticsHelper which does nothing.
    // This is so that tests and previews do not have to provide one.
    NoOpAnalyticsHelper()
}
