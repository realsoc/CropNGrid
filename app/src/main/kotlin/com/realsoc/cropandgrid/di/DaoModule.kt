package com.realsoc.cropandgrid.di

import com.realsoc.cropandgrid.room.CropNGridDatabase
import com.realsoc.cropandgrid.room.GridDao
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