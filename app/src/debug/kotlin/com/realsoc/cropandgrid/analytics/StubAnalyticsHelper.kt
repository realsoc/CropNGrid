package com.realsoc.cropandgrid.analytics

import android.util.Log
import com.realsoc.cropandgrid.analytics.AnalyticsEvent.Types.LOG_GRANTED
import com.realsoc.cropandgrid.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "StubAnalyticsHelper"

@Singleton
class StubAnalyticsHelper @Inject constructor(
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