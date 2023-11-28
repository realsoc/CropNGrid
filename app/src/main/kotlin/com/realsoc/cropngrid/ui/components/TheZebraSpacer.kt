package com.realsoc.cropngrid.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun TheZebraSpacer(
    color: Color,
    modifier: Modifier = Modifier,
    lineSpace: Dp = 10.dp,
    strokeWidth: Dp = 3.dp
) {
    with(LocalDensity.current) {
        val gapBetweenLines = (lineSpace + strokeWidth).toPx()
        Spacer(modifier = modifier
            .drawWithContent {
                val lineCount = ceil(size.width / gapBetweenLines).toInt()
                val lineLength = sqrt(size.height.pow(2) + size.width.pow(2))
                val startY = center.y - lineLength / 2f
                val endY = center.y + lineLength / 2f
                clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height
                ) {
                    rotate(45f, center) {
                        (-1..lineCount)
                            .map { it * gapBetweenLines }
                            .map { x ->
                                drawLine(
                                    color = color,
                                    start = Offset(x, startY),
                                    end = Offset(x, endY),
                                    strokeWidth = strokeWidth.toPx()
                                )
                            }
                    }
                }
            }
        )
    }
}
