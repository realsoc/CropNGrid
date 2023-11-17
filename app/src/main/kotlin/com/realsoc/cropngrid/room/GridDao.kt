package com.realsoc.cropngrid.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.models.GridUris
import kotlinx.coroutines.flow.Flow

@Dao
interface GridDao {
    @Insert
    suspend fun insertAll(vararg grids: Grid)

    @Delete
    suspend fun delete(grid: Grid)

    @Query("SELECT * FROM grids")
    suspend fun getAll(): List<Grid>

    @Query("SELECT * FROM grids WHERE id = :id")
    fun loadGridById(id: String): Flow<Grid>

    @Query("SELECT * from grids")
    fun loadGrids(): Flow<List<Grid>>
}


class Converters {
    @TypeConverter
    fun fromJson(parts: String): GridUris {
        return Gson().fromJson(parts)
    }

    @TypeConverter
    fun toJson(parts: GridUris): String {
        return Gson().toJson(parts)
    }
}