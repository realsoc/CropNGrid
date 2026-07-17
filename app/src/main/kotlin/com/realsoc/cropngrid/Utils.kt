package com.realsoc.cropngrid

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.exifinterface.media.ExifInterface
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.math.roundToInt

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

fun decode(input : String): String = URLDecoder.decode(input, URL_CHARACTER_ENCODING)

fun encode(input: String): String = URLEncoder.encode(input, URL_CHARACTER_ENCODING)


fun String.toUri(): Uri {
    return Uri.parse(this)
}

val Bitmap.frame: Rect
    get() = Rect(Offset(0f, 0f), size)

fun Bitmap.safeRecycle() {
    if (!isRecycled) {
        recycle()
    }
}

// Larger bitmaps cannot be rendered by the hardware canvas and get close to the app memory budget
const val MAX_BITMAP_DIMENSION = 4096

suspend fun ContentResolver.getBitmap(uri: Uri, maxDimension: Int = MAX_BITMAP_DIMENSION): Bitmap =
    withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(this@getBitmap, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                // Software allocation so the bitmap can be drawn on the crop canvas without copying
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val maxDim = maxOf(info.size.width, info.size.height)
                if (maxDim > maxDimension) {
                    decoder.setTargetSampleSize(Integer.highestOneBit(maxDim / maxDimension).coerceAtLeast(1))
                }
            }
        } else {
            decodeDownsampled(uri, maxDimension)
        }
    }

private fun ContentResolver.decodeDownsampled(uri: Uri, maxDimension: Int): Bitmap {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        ?: throw IOException("Could not open $uri")

    var sampleSize = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= maxDimension) {
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val bitmap = openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        ?: throw IOException("Could not decode $uri")

    // BitmapFactory does not apply EXIF orientation (ImageDecoder on P+ does)
    val rotation = openInputStream(uri)?.use { ExifInterface(it).rotationDegrees } ?: 0
    if (rotation == 0) return bitmap

    val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        .also { rotated -> if (rotated !== bitmap) bitmap.recycle() }
}


val Bitmap.size: Size get() = Size(width.toFloat(), height.toFloat())

fun Size.toOffset(): Offset {
    return Offset(width, height)
}

operator fun IntSize.times(scale: Float): Size {
    return Size(width * scale, height * scale)
}


private operator fun Rect.times(scale: Float): Rect {
    return Rect(offset = topLeft * scale, size = size * scale)
}

fun Rect.scale(scale: Float, pivot: Offset): Rect {
    return Rect(topLeft.scale(scale, pivot), size * scale)
}


fun Offset.scale(scale: Float, pivot: Offset): Offset {
    return pivot + (this - pivot) * scale
}

operator fun Offset.plus(size: Size): Offset {
    return Offset(x + size.width, y + size.height)
}

operator fun Offset.minus(size: Size): Offset {
    return Offset(x - size.width, y - size.height)
}

operator fun Size.minus(other: Size): Offset {
    return Offset(width - other.width, height - other.height)
}

operator fun Size.plus(other: Size): Size {
    return Size(width + other.width, height + other.height)
}

suspend fun createBitmapList(
    source: Bitmap,
    areas: List<List<Rect>>,
    coordinateSystem: CoordinateSystem
): List<List<Bitmap>> = withContext(Dispatchers.Default) {
    // A hardware bitmap cannot be drawn on a software canvas: copy it once for all areas
    val softwareSource = source.asSoftwareBitmap()
    try {
        areas.map { it.map { rect -> createBitmapOfArea(softwareSource, rect, coordinateSystem) } }
    } finally {
        if (softwareSource !== source) softwareSource.recycle()
    }
}

suspend fun createBitmapOfArea(
    source: Bitmap,
    area: Rect,
    coordinateSystem: CoordinateSystem
): Bitmap = withContext(Dispatchers.Default) {
    val width = area.width.roundToInt().coerceAtLeast(1)
    val height = area.height.roundToInt().coerceAtLeast(1)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val softwareSource = source.asSoftwareBitmap()

    try {
        val canvas = android.graphics.Canvas()

        canvas.setBitmap(bitmap)

        canvas.translate(
            coordinateSystem.transformation.translation.x - area.topLeft.x,
            coordinateSystem.transformation.translation.y - area.topLeft.y)
        canvas.rotate(coordinateSystem.transformation.rotation, coordinateSystem.pivot.x, coordinateSystem.pivot.y)
        canvas.scale(
            coordinateSystem.transformation.scale,
            coordinateSystem.transformation.scale,
            coordinateSystem.pivot.x,
            coordinateSystem.pivot.y
        )

        canvas.drawBitmap(softwareSource, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG))
    } finally {
        if (softwareSource !== source) softwareSource.recycle()
    }

    return@withContext bitmap
}

private fun Bitmap.asSoftwareBitmap(): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.HARDWARE) {
        copy(Bitmap.Config.ARGB_8888, false)
    } else {
        this
    }