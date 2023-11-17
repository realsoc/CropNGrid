package com.realsoc.cropngrid.di

import com.realsoc.cropngrid.analytics.AnalyticsHelper
import com.realsoc.cropngrid.analytics.StubAnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun bindsAnalyticsHelper(
        analyticsHelperImpl: StubAnalyticsHelper
    ): AnalyticsHelper
}