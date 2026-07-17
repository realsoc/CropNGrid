package com.realsoc.cropandgrid.ui.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GridParametersTest {

    @Test
    fun `gridRatio for item mode combines ratio with column and row count`() {
        val parameters = GridParameters(columnNumber = 3, rowNumber = 5, ratio = 1f)
        assertEquals(3f / 5f, parameters.gridRatio, 1e-6f)
    }

    @Test
    fun `gridRatio for image mode is the raw ratio`() {
        val parameters = GridParameters(
            columnNumber = 3,
            rowNumber = 5,
            ratio = 0.75f,
            ratioMode = GridParameters.RatioMode.RatioForImage
        )
        assertEquals(0.75f, parameters.gridRatio, 1e-6f)
    }

    @Test
    fun `getWidthAndHeight in item mode fills the limiting side`() {
        val parameters = GridParameters(columnNumber = 3, rowNumber = 3, ratio = 1f)
        val (width, height) = parameters.getWidthAndHeight(900f)

        // Square cells in a 3x3 grid: the grid is square and fills the limiting side
        assertEquals(900f, width, 1e-3f)
        assertEquals(900f, height, 1e-3f)
    }

    @Test
    fun `getWidthAndHeight in item mode with more rows than columns is height bound`() {
        val parameters = GridParameters(columnNumber = 1, rowNumber = 5, ratio = 1f)
        val (width, height) = parameters.getWidthAndHeight(1000f)

        assertEquals(1000f, height, 1e-3f)
        assertEquals(200f, width, 1e-3f)
    }

    @Test
    fun `getWidthAndHeight in image mode respects portrait ratio`() {
        val parameters = GridParameters(
            ratio = 0.5f,
            ratioMode = GridParameters.RatioMode.RatioForImage
        )
        val (width, height) = parameters.getWidthAndHeight(800f)

        assertEquals(800f, height, 1e-3f)
        assertEquals(400f, width, 1e-3f)
    }

    @Test
    fun `ratioAsString returns the preset label`() {
        assertEquals("1:1", GridParameters(ratio = 1f).ratioAsString)
        assertEquals("16:9", GridParameters(ratio = 16 / 9f).ratioAsString)
    }

    @Test
    fun `ratioAsString falls back to a formatted value for non presets`() {
        // Must not throw for ratios outside the preset list
        val label = GridParameters(ratio = 0.123f).ratioAsString
        assertTrue(label.isNotBlank())
    }
}
