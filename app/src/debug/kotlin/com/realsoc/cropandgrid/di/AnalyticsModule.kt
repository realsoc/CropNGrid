package com.realsoc.cropandgrid.di

import com.realsoc.cropandgrid.analytics.AnalyticsHelper
import com.realsoc.cropandgrid.analytics.StubAnalyticsHelper
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