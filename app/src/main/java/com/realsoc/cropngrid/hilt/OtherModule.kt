package com.realsoc.cropngrid.hilt

import com.realsoc.cropngrid.GridRepository
import com.realsoc.cropngrid.GridRepositoryImpl
import com.realsoc.cropngrid.PictureRepository
import com.realsoc.cropngrid.PictureRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindGridRepository(
        gridRepository: GridRepositoryImpl,
    ): GridRepository

    @Binds
    fun bindPictureRepository(
        pictureRepository: PictureRepositoryImpl,
    ): PictureRepository
}