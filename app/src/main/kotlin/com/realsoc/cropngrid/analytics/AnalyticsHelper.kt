package com.realsoc.cropngrid.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.realsoc.cropngrid.analytics.AnalyticsEvent.Param
import com.realsoc.cropngrid.analytics.AnalyticsEvent.ParamKeys
import com.realsoc.cropngrid.analytics.AnalyticsEvent.Types

// Thanks to https://aslansari.com/posts/firebase-analytics-w-compose/
interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)

}

fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

fun AnalyticsHelper.logDialogDisplayed(dialogName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.DIALOG_DISPLAYED,
            extras = listOf(
                Param(ParamKeys.DIALOG_NAME, dialogName),
            ),
        ),
    )
}

fun AnalyticsHelper.logPermissionGranted(type: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.PERMISSION_GRANTED,
            extras = listOf(
                Param(ParamKeys.TYPE, type),
            ),
        ),
    )
}

fun AnalyticsHelper.logPermissionRecused(type: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.PERMISSION_RECUSED,
            extras = listOf(
                Param(ParamKeys.TYPE, type),
            ),
        ),
    )
}

fun AnalyticsHelper.logPermissionIntentLaunched() {
    logEvent(
        AnalyticsEvent(
            type = Types.PERMISSION_INTENT_LAUNCHED,
        ),
    )
}

fun AnalyticsHelper.logPermissionAsked(type: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.PERMISSION_ASKED,
            extras = listOf(
                Param(ParamKeys.TYPE, type),
            ),
        ),
    )
}

fun AnalyticsHelper.logLogGranted(value: Boolean) {
    logEvent(
        AnalyticsEvent(
            type = Types.LOG_GRANTED,
            extras = listOf(
                Param(ParamKeys.VALUE, value.toString()),
            ),
        ),
    )
}

fun AnalyticsHelper.buttonClick(screenName: String, buttonId: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.BUTTON_CLICK,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
                Param(ParamKeys.BUTTON_ID, buttonId),
            ),
        ),
    )
}

fun AnalyticsHelper.gridCropped(columnCount: Int, rowCount: Int) {
    logEvent(
        AnalyticsEvent(
            type = Types.GRID_CROPPED,
            extras = listOf(
                Param(ParamKeys.COLUMN_COUNT, columnCount.toString()),
                Param(ParamKeys.ROW_COUNT, rowCount.toString()),
            ),
        ),
    )
}

@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}

@Composable
fun TrackDialogDisplayed(
    dialogName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logDialogDisplayed(dialogName)
    onDispose {}
}