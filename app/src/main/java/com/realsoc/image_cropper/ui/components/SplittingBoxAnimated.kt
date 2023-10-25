package com.realsoc.image_cropper.ui.components

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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.times
import kotlin.math.min


@Composable
fun SplittingBoxAnimated(
    toShowLeftToRightTopToBottom: List<Bitmap>,
    numberColumn: Int,
    numberRow: Int,
    boxSize: Rect,
    modifier: Modifier = Modifier
) {
    if (toShowLeftToRightTopToBottom.size != numberColumn * numberRow) {
        println("the bitmap list should be of " +
                "size ${numberColumn * numberRow} instead of what it is size ${toShowLeftToRightTopToBottom.size}")
    }
    BoxWithConstraints(
        modifier
            .fillMaxSize()
    ) {
        val maxOffsetAnim = 1f
        val maxOffset = maxOffsetAnim * 60

        val infiniteTransition = rememberInfiniteTransition(label = "box splitting transition")
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = maxOffsetAnim,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutCirc),
                repeatMode = RepeatMode.Reverse
            ), label = "offset between box animation"
        )

        // We have an IMAGE made of moving ITEMs (ANIMATED BOX) that will animate in CONSTRAINTS

        // What is the scale necessary to make the max size fit CONSTRAINTS

        val maxRealWidth = numberColumn * (boxSize.width + maxOffset) - maxOffset
        val maxRealHeight = numberRow * (boxSize.height + maxOffset) - maxOffset

        val scaleToFitWidth = constraints.maxWidth / maxRealWidth
        val scaleToFitHeight = constraints.maxHeight / maxRealHeight

        val scale = min(scaleToFitWidth, scaleToFitHeight)

        // Let's scale everything with scale to fit the constraints

        val currentAnimatedBoxWidth = ((numberColumn * (boxSize.width + offset *60) - offset*60) * scale).toInt()
        val currentAnimatedBoxHeight = ((numberRow * (boxSize.height + offset*60) - offset*60) * scale).toInt()

        // Used to center the animated box in the dialog
        val diffHeight = constraints.maxHeight - currentAnimatedBoxHeight
        val diffWidth = constraints.maxWidth -  currentAnimatedBoxWidth

        // From here, all the value are calculated in dp
        val scaledOffset = with(LocalDensity.current) { (scale * offset*60).toInt().toDp() }

        val (itemWidth, itemHeight) = with(LocalDensity.current) {
            (boxSize.width * scale).toDp() to (boxSize.height * scale).toDp()
        }

        toShowLeftToRightTopToBottom.forEachIndexed { position, image ->

            val itemHorizontalPosition = position % numberColumn
            val itemVerticalPosition = position / numberColumn

            val itemTop = itemVerticalPosition * (itemHeight + scaledOffset)
            val itemFloat = itemHorizontalPosition * (itemWidth + scaledOffset)

            Box(modifier = Modifier
                .offset(itemFloat, itemTop)
                .size(itemWidth, itemHeight)
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0f, 0f)
                    translationX = diffWidth / 2f
                    translationY = diffHeight / 2f
                }
            ) {
                Image(image.asImageBitmap(), "image at position ($itemHorizontalPosition,$itemVerticalPosition)")
            }
        }
    }
}