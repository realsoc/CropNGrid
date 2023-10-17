package com.realsoc.image_cropper

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import com.realsoc.image_cropper.ui.Point
import com.realsoc.image_cropper.ui.TransformationState
import com.realsoc.image_cropper.ui.plus
import com.realsoc.image_cropper.ui.times
import com.realsoc.image_cropper.ui.toPoint
import com.realsoc.image_cropper.ui.vectorTo
import java.net.URLEncoder
import kotlin.math.cos
import kotlin.math.sin

fun Uri.encode(): String {
    return URLEncoder.encode(toString(), "UTF-8")
}

fun String.toUri(): Uri {
    return Uri.parse(this)
}

val Bitmap.frame: Rect
    get() = Rect(Offset(0f, 0f), size)

@Suppress("DEPRECATION")
fun ContentResolver.getBitmap(uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(this, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(this, uri)
    }
}

/**
 * For a ratio, return width and height respecting max(width, height) == biggestSide
 */
fun getWidthAndLengthFromRatio(biggestSide: Float, ratio: Float): Pair<Float, Float> {
    return if (ratio < 1f) {
        (biggestSide * ratio) to biggestSide
    } else {
        biggestSide to (biggestSide / ratio)
    }
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
    //val vectorToTopLeft =
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