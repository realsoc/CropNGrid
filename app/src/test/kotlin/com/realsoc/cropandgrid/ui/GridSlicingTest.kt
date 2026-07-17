package com.realsoc.cropandgrid.ui

import androidx.compose.ui.geometry.Rect
import com.realsoc.cropandgrid.ui.models.GridParameters
import org.junit.Assert.assertEquals
import org.junit.Test

class GridSlicingTest {

    @Test
    fun `getCropGrid produces the requested number of cells`() {
        val grid = getCropGrid(Rect(0f, 0f, 300f, 500f), GridParameters(columnNumber = 3, rowNumber = 5))

        assertEquals(5, grid.size)
        grid.forEach { row -> assertEquals(3, row.size) }
    }

    @Test
    fun `getCropGrid cells tile the area with no gaps or overlaps`() {
        val area = Rect(10f, 20f, 310f, 520f)
        val grid = getCropGrid(area, GridParameters(columnNumber = 3, rowNumber = 5))

        // Adjacent cells share exact boundaries
        grid.forEach { row ->
            row.zipWithNext().forEach { (left, right) ->
                assertEquals(left.right, right.left, 0f)
            }
        }
        grid.zipWithNext().forEach { (topRow, bottomRow) ->
            topRow.zip(bottomRow).forEach { (top, bottom) ->
                assertEquals(top.bottom, bottom.top, 0f)
            }
        }

        // The outer edges match the requested area
        assertEquals(area.left, grid.first().first().left, 0f)
        assertEquals(area.top, grid.first().first().top, 0f)
        assertEquals(area.right, grid.last().last().right, 1e-4f)
        assertEquals(area.bottom, grid.last().last().bottom, 1e-4f)
    }

    @Test
    fun `calculateGridArea centers the grid in the available space`() {
        val area = calculateGridArea(GridParameters(columnNumber = 3, rowNumber = 3, ratio = 1f), 1000f, 800f)

        // Square 3x3 grid limited by the 800px height
        assertEquals(800f, area.width, 1e-3f)
        assertEquals(800f, area.height, 1e-3f)
        assertEquals(100f, area.left, 1e-3f)
        assertEquals(0f, area.top, 1e-3f)
    }
}
