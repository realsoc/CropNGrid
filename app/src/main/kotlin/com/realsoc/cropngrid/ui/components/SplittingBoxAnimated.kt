package com.realsoc.cropngrid.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.EaseInOutCirc
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.times
import com.realsoc.cropngrid.frame
import kotlin.math.min


@Composable
fun SplittingBoxAnimated(
    toShowLeftToRightTopToBottom: List<List<Bitmap>>,
    modifier: Modifier = Modifier
) {

    if (toShowLeftToRightTopToBottom.isNotEmpty() && toShowLeftToRightTopToBottom.first().isNotEmpty()) {
        val rowCount = toShowLeftToRightTopToBottom.size
        val columnCount = toShowLeftToRightTopToBottom.first().size
        val boxSize = toShowLeftToRightTopToBottom.first().first().frame
        BoxWithConstraints(
            modifier
                .fillMaxSize()
        ) {
                val maxOffset = 60f

                val infiniteTransition = rememberInfiniteTransition(label = "box splitting transition")
                val offset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = maxOffset,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = EaseInOutCirc),
                        repeatMode = RepeatMode.Reverse
                    ), label = "offset between box animation"
                )

                // We have an IMAGE made of moving ITEMs (ANIMATED BOX) that will animate in CONSTRAINTS

                // What is the scale necessary to make the max size fit CONSTRAINTS

                val maxRealWidth = columnCount * (boxSize.width + maxOffset) - maxOffset
                val maxRealHeight = rowCount * (boxSize.height + maxOffset) - maxOffset

                val scaleToFitWidth = constraints.maxWidth / maxRealWidth
                val scaleToFitHeight = constraints.maxHeight / maxRealHeight

                val scale = min(scaleToFitWidth, scaleToFitHeight)

                // Let's scale everything to fit the constraints

                val currentAnimatedBoxWidth = ((columnCount * (boxSize.width + offset) - offset) * scale)
                val currentAnimatedBoxHeight = ((rowCount * (boxSize.height + offset) - offset) * scale)

                // Used to center the animated box in the dialog
                val diffHeight = constraints.maxHeight - currentAnimatedBoxHeight
                val diffWidth = constraints.maxWidth -  currentAnimatedBoxWidth

                // From here, all the value are calculated in dp
                val scaledOffset = with(LocalDensity.current) { (scale * offset).toInt().toDp() }

                val (itemWidth, itemHeight) = with(LocalDensity.current) {
                    (boxSize.width * scale).toDp() to (boxSize.height * scale).toDp()
                }

                toShowLeftToRightTopToBottom.mapIndexed { rowNumber, row ->
                    row.mapIndexed { columnNumber, bitmap ->
                        val itemTop = rowNumber * (itemHeight + scaledOffset)
                        val itemFloat = columnNumber * (itemWidth + scaledOffset)

                        Box(modifier = Modifier
                            .offset(itemFloat, itemTop)
                            .size(itemWidth, itemHeight)
                            .graphicsLayer {
                                transformOrigin = TransformOrigin(0f, 0f)
                                translationX = diffWidth / 2
                                translationY = diffHeight / 2
                            }
                        ) {
                            Image(
                                bitmap.asImageBitmap(),
                                "image at position ($rowNumber,$columnNumber)",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
    }
}