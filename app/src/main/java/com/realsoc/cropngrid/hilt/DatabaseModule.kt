package com.realsoc.cropngrid.hilt

import android.content.Context
import androidx.room.Room
import com.realsoc.cropngrid.room.CropNGridDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): CropNGridDatabase {
        return Room.databaseBuilder(
            appContext,
            CropNGridDatabase::class.java,
            "cropngrid_database"
        ).build()
    }
}