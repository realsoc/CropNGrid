package com.realsoc.cropngrid.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {

    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // An uncaught exception in this app-wide scope must not crash the process
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.e("CropNGrid", "Uncaught exception in application scope", throwable)
        }
        return CoroutineScope(SupervisorJob() + Dispatchers.Default + exceptionHandler)
    }
}