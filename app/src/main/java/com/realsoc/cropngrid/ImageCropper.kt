package com.realsoc.cropngrid

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class ImageCropper {
    suspend fun cropImage(
        source: String,
        destination: String,
        xOffset: Int,
        yOffset: Int,
        outWidth: Int,
        outHeight: Int
    ) {
        withContext(Dispatchers.IO) {
            flow<Bitmap> { loadImage(source) }
                .map { bitmap -> cropImage(bitmap, xOffset, yOffset, outWidth, outHeight) }
                .map { croppedBitmap -> saveImage(croppedBitmap, destination) }
                .collect()
        }
    }

    fun cropImage(
        image: Bitmap,
        xOffset: Int,
        yOffset: Int,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return Bitmap.createBitmap(image, xOffset, yOffset, outWidth, outHeight)
    }

    private fun loadImage(source: String): Bitmap {
        return BitmapFactory.decodeFile(source).also {
            if (it == null) throw Exception("$source coulnd't be decoded")
        }
    }

    private suspend fun saveImage(bitmap: Bitmap, destination: String) {
        val fOS = FileOutputStream(destination)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOS)
        throw Error("Error while saving bitmap to $destination")
    }
}

