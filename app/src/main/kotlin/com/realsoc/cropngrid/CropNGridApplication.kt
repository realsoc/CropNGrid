package com.realsoc.cropngrid

import android.app.Application
import com.realsoc.cropngrid.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CropNGridApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CropNGridApplication)
            androidLogger()
            modules(appModule)
        }
    }
}