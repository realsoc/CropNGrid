package com.realsoc.cropandgrid.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.realsoc.cropandgrid.models.Grid

@Database(entities = [Grid::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CropNGridDatabase : RoomDatabase() {
    abstract fun gridDao(): GridDao
}
