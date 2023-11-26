package com.realsoc.cropngrid.di

import androidx.room.Room
import com.realsoc.cropngrid.analytics.AnalyticsHelper
import com.realsoc.cropngrid.analytics.NoOpAnalyticsHelper
import com.realsoc.cropngrid.data.GridRepository
import com.realsoc.cropngrid.data.GridRepositoryImpl
import com.realsoc.cropngrid.data.PictureRepository
import com.realsoc.cropngrid.data.PictureRepositoryImpl
import com.realsoc.cropngrid.data.PreferencesRepository
import com.realsoc.cropngrid.data.PreferencesRepositoryImpl
import com.realsoc.cropngrid.room.CropNGridDatabase
import com.realsoc.cropngrid.viewmodels.CropperViewModel
import com.realsoc.cropngrid.viewmodels.GridListViewModel
import com.realsoc.cropngrid.viewmodels.GridViewModel
import com.realsoc.cropngrid.viewmodels.InfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single {
        val db = Room.databaseBuilder(androidApplication(), CropNGridDatabase::class.java, "cropngrid_database").build()
        db.gridDao()
    }

    singleOf(::GridRepositoryImpl) { bind<GridRepository>() }
    singleOf(::PictureRepositoryImpl) { bind<PictureRepository>() }
    singleOf(::PreferencesRepositoryImpl) { bind<PreferencesRepository>() }
    singleOf(::NoOpAnalyticsHelper) { bind<AnalyticsHelper>() }

    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    viewModelOf(::CropperViewModel)
    viewModelOf(::GridListViewModel)
    viewModelOf(::GridViewModel)
    viewModelOf(::InfoViewModel)

}