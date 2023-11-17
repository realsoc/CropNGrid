package com.realsoc.cropngrid.di

import com.realsoc.cropngrid.room.CropNGridDatabase
import com.realsoc.cropngrid.room.GridDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideGridDao(appDatabase: CropNGridDatabase): GridDao = appDatabase.gridDao()

}