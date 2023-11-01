package com.realsoc.cropngrid

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
import com.realsoc.cropngrid.ui.models.CoordinateSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.URLEncoder

fun Uri.encode(): String {
    return URLEncoder.encode(toString(), "UTF-8")
}

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

@Suppress("DEPRECATION")
fun ContentResolver.getBitmap(uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(this, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(this, uri)
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

suspend fun createBitmapList(
    source: Bitmap,
    areas: List<Rect>,
    coordinateSystem: CoordinateSystem
): List<Bitmap> = withContext(Dispatchers.IO) {
    return@withContext areas.map {
        async {
            createBitmapOfArea(source, it, coordinateSystem)
        }
    }.awaitAll()
}

suspend fun createBitmapOfArea(
    source: Bitmap,
    area: Rect,
    coordinateSystem: CoordinateSystem
): Bitmap = withContext(Dispatchers.IO){
    val size = Size(area.width, area.height)

    val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
    @Suppress("NAME_SHADOWING") val source = source.copy(Bitmap.Config.ARGB_8888, true)

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

    canvas.drawBitmap(source, 0f, 0f, null)

    return@withContext bitmap
}