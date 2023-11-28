package com.realsoc.cropngrid.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.realsoc.cropngrid.analytics.AnalyticsEvent.Types.LOG_GRANTED
import com.realsoc.cropngrid.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val coroutineScope: CoroutineScope
) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        coroutineScope.launch {
            val logGranted = preferencesRepository.getLogGranted().first()
            if (logGranted || event.type == LOG_GRANTED) {
                firebaseAnalytics.logEvent(event.type) {
                    for (extra in event.extras) {
                        param(
                            key = extra.key.take(40),
                            value = extra.value.take(100),
                        )
                    }
                }
            }
        }
    }
}