package com.realsoc.cropngrid.ui.models

import kotlin.math.abs


data class GridParameters(
    val columnNumber: Int = 3,
    val rowNumber: Int = 3,
    val ratio: Float = 1f,
    val ratioMode: RatioMode = RatioMode.RatioForItem
) {

    val gridRatio: Float
        get() {
            return if (ratioMode == RatioMode.RatioForImage) {
                ratio
            } else {
                ratio * columnNumber / rowNumber
            }
        }

    val ratioAsString: String
        get() = RATIO_VALUES.first { abs(it.second - ratio) < 0.001f }.first

    fun getWidthAndHeight(limitingSide: Float): Pair<Float, Float> {
        var width = 1f
        var height = 1f

        when (ratioMode) {
            RatioMode.RatioForImage -> {
                if (ratio < 1f) {
                    width = limitingSide * ratio
                    height = limitingSide
                } else {
                    width = limitingSide
                    height = limitingSide / ratio
                }
            }

            RatioMode.RatioForItem -> {
                // itemW / itemH = ratio
                // itemW = ratio * itemH
                // (gridAreaW X gridAreaH) = (itemW * ColNum X itemH * rowNum)
                // (gridAreaW X gridAreaH) = (ratio * itemH * ColNum X itemH * rowNum)
                // (gridAreaW X gridAreaH) = ((ratio * ColNum) * itemH *  X rowNum * itemH)
                // Which one is limitingSide ??

                val widthFactor = ratio * columnNumber
                val heightFactor = rowNumber

                val itemHeight: Float

                if (widthFactor < heightFactor) {
                    // Height is the biggest side
                    height = limitingSide
                    itemHeight = height / rowNumber
                    width = itemHeight * (ratio * columnNumber)
                } else {
                    // Width is the biggest side
                    width = limitingSide
                    itemHeight = width / (ratio * columnNumber)
                    height = itemHeight * rowNumber
                }

            }
        }

        return width to height
    }

    sealed class RatioMode {
        data object RatioForItem : RatioMode()
        data object RatioForImage : RatioMode()
    }

    companion object {
        val RATIO_VALUES: List<Pair<String, Float>> = listOf(
            "6:13" to 6/13f,
            "9:16" to 9/16f,
            "1:1" to 1f,
            "5:4" to 5/4f,
            "4:3" to 4/3f,
            "3:2" to 3/2f,
            "8:5" to 8/5f,
            "φ" to 1.618f,
            "16:9" to 16/9f
        )
    }
}