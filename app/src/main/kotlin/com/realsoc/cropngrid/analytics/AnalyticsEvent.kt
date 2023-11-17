package com.realsoc.cropngrid.analytics

data class AnalyticsEvent(
    val type: String,
    val extras: List<Param> = emptyList(),
) {
    object Types {
        const val SCREEN_VIEW = "screen_view"
        const val DIALOG_DISPLAYED = "dialog_displayed"
        const val LOG_GRANTED = "log_granted"
        const val BUTTON_CLICK = "button_click"
        const val GRID_CROPPED = "grid_cropped"
        const val PERMISSION_ASKED = "permission_asked" // (extras: type)
        const val PERMISSION_GRANTED = "permission_granted" // (extras: type)
        const val PERMISSION_RECUSED = "permission_recused"
        const val PERMISSION_INTENT_LAUNCHED = "permission_intent_launched"
    }

    data class Param(val key: String, val value: String)

    object ParamKeys {
        const val VALUE = "value"
        const val SCREEN_NAME = "screen_name"
        const val BUTTON_ID = "button_id"
        const val TYPE = "type"
        const val DIALOG_NAME = "dialog_name"
        const val ROW_COUNT = "row_count"
        const val COLUMN_COUNT = "column_count"
    }
}
