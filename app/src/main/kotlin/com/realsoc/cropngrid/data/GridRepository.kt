package com.realsoc.cropngrid.data

import androidx.annotation.WorkerThread
import com.realsoc.cropngrid.models.Grid
import com.realsoc.cropngrid.room.GridDao
import kotlinx.coroutines.flow.Flow

interface GridRepository {
    fun getGrids(): Flow<List<Grid>>

    fun getGrid(id: String): Flow<Grid>

    suspend fun addGrid(grid: Grid)

    suspend fun deleteGrid(grid: Grid)
}
class GridRepositoryImpl(private val gridDao: GridDao): GridRepository {
    override fun getGrids(): Flow<List<Grid>> {
        return gridDao.loadGrids()
    }

    override fun getGrid(id: String): Flow<Grid> {
        return gridDao.loadGridById(id)
    }

    @WorkerThread
    override suspend fun addGrid(grid: Grid) {
        gridDao.insertAll(grid)
    }

    @WorkerThread
    override suspend fun deleteGrid(grid: Grid) {
        gridDao.delete(grid)
    }
}
