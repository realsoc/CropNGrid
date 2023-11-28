package com.realsoc.cropngrid.analytics

import android.util.Log
import com.realsoc.cropngrid.analytics.AnalyticsEvent.Types.LOG_GRANTED
import com.realsoc.cropngrid.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "StubAnalyticsHelper"

class StubAnalyticsHelper (
    private val preferencesRepository: PreferencesRepository,
    private val coroutineScope: CoroutineScope
) :
    AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        coroutineScope.launch {
            val logGranted = preferencesRepository.getLogGranted().first()
            if (logGranted || event.type == LOG_GRANTED) {
                Log.d(TAG, "Received analytics event: $event")
            }
        }
    }
}