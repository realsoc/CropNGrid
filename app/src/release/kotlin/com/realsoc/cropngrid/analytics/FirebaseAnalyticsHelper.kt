package com.realsoc.cropngrid.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.realsoc.cropngrid.CropNGridApplication
import com.realsoc.cropngrid.analytics.AnalyticsEvent.Types.LOG_GRANTED
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    private val application: CropNGridApplication,
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        if (application.logGrantedState.value || event.type == LOG_GRANTED) {
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