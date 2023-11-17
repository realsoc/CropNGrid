package com.realsoc.cropngrid.di

import com.realsoc.cropngrid.data.GridRepository
import com.realsoc.cropngrid.data.GridRepositoryImpl
import com.realsoc.cropngrid.data.PictureRepository
import com.realsoc.cropngrid.data.PictureRepositoryImpl
import com.realsoc.cropngrid.data.PreferencesRepository
import com.realsoc.cropngrid.data.PreferencesRepositoryImpl
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