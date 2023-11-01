package com.realsoc.cropngrid

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext

@Composable
fun BitmapTransformTest() {
    var bitmap by remember { mutableStateOf<Bitmap?>( null) }
    var x by remember { mutableFloatStateOf(0f) }
    var y by remember { mutableFloatStateOf(0f) }
    var maxX by remember { mutableFloatStateOf(0f) }
    var maxY by remember { mutableFloatStateOf(0f) }
    var angle by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var showTransformedBitmap by remember { mutableStateOf(true) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        bitmap = context.testBitmap.apply {
            val height = height
            val width = width

            maxX = width.toFloat()
            maxY = height.toFloat()
        }
    }
    Column {
        Text(if (showTransformedBitmap) "Transformed image" else "Base Image")
        Box(
            Modifier
                .weight(1f)
        ) {
            bitmap?.let { baseBitmap ->

                NativeCanvasSample2(
                    bitmap = baseBitmap,
                    modifier = Modifier,
                    angle = angle,
                    offsetX = x,
                    offsetY = y,
                    scale = scale
                )
            }

        }
        Column(Modifier.weight(1f)) {
            SliderLabeled(label = "X", value = x, onValueChange = { x = it }, valueRange = -maxX..maxX)
            SliderLabeled(label = "Y", value = y, onValueChange = { y = it }, valueRange = -maxY..maxY)
            SliderLabeled(label = "Angle", value = angle, onValueChange = { angle = it }, valueRange = 0f..360f)
            SliderLabeled(label = "Scale", value = scale, onValueChange = { scale = it }, valueRange = 0.5f..4f)
            Checkbox(checked = showTransformedBitmap, onCheckedChange = { showTransformedBitmap = !showTransformedBitmap })
        }
    }
}

@Composable
fun NativeCanvasSample2(bitmap: Bitmap, modifier: Modifier, angle: Float, offsetX: Float, offsetY: Float,
                        scale: Float) {

    BoxWithConstraints(modifier) {

        val imageWidth = constraints.maxWidth
        val imageHeight = constraints.maxHeight

        androidx.compose.foundation.Canvas(Modifier) {
            clipRect(0f, 0f, imageWidth.toFloat(), imageHeight.toFloat()) {
                translate(offsetX, offsetY) {
                    rotate(angle) {
                        scale(scale) {
                            drawImage(bitmap.asImageBitmap())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SliderLabeled(modifier: Modifier = Modifier, label: String, value: Float, onValueChange: (Float) -> Unit,
                  valueRange:
ClosedFloatingPointRange<Float>) {
    Column(modifier.fillMaxWidth()) {
        Text(label)
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange)
    }
}


val Context.testBitmap: Bitmap
    get() {
        return BitmapFactory.decodeStream(resources.assets.open("test_image.png"))
    }