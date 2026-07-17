package com.realsoc.cropandgrid.di

import com.realsoc.cropandgrid.data.GridRepository
import com.realsoc.cropandgrid.data.GridRepositoryImpl
import com.realsoc.cropandgrid.data.PictureRepository
import com.realsoc.cropandgrid.data.PictureRepositoryImpl
import com.realsoc.cropandgrid.data.PreferencesRepository
import com.realsoc.cropandgrid.data.PreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindGridRepository(
        gridRepository: GridRepositoryImpl,
    ): GridRepository

    @Binds
    fun bindPictureRepository(
        pictureRepository: PictureRepositoryImpl,
    ): PictureRepository

    @Singleton
    @Binds
    fun bindPreferencesRepository(
        preferencesRepository: PreferencesRepositoryImpl
    ): PreferencesRepository
}