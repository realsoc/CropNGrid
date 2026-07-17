package com.realsoc.cropngrid.data

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


interface PictureRepository {
    /**
     * Saves the bitmap and returns its Uri, or throws on failure.
     */
    @Throws(IOException::class)
    suspend fun saveImage(
        context: Application,
        bitmap: Bitmap,
        name: String,
        sharable: Boolean = false
    ): Uri
}

class PictureRepositoryImpl @Inject constructor() : PictureRepository {

    override suspend fun saveImage(
        context: Application, bitmap: Bitmap, name: String,
        sharable: Boolean
    ): Uri = withContext(Dispatchers.IO) {
        if (sharable) {
            if (SDK_INT >= Build.VERSION_CODES.Q) {
                savePublicImageInAndroidApi29AndAbove(context.contentResolver, bitmap, name)
            } else {
                savePublicImageInAndroidApi28AndBelow(context, bitmap, name)
            }
        } else {
            savePrivateImage(context, bitmap, name)
        }
    }

    @Throws(IOException::class)
    private fun savePublicImageInAndroidApi28AndBelow(context: Context, bitmap: Bitmap, name: String): Uri {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        imagesDir.mkdirs()
        val image = File(imagesDir, "$name.png")
        image.writeBitmap(bitmap)
        // Make the file visible to gallery and file manager apps
        MediaScannerConnection.scanFile(context, arrayOf(image.absolutePath), arrayOf("image/png"), null)

        return Uri.fromFile(image)
    }

    @Throws(IOException::class)
    private fun savePublicImageInAndroidApi29AndAbove(resolver: ContentResolver, bitmap: Bitmap, name: String): Uri {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            // Hide the entry from other apps until it is fully written
            values.put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        var uri: Uri? = null
        return try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, values)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            resolver.openOutputStream(uri).use { stream ->
                if (stream == null) {
                    throw IOException("Failed to open output stream.")
                }
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                    throw IOException("Failed to save bitmap.")
                }
            }
            if (SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            uri
        } catch (e: Exception) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            throw e
        }
    }

    @Throws(IOException::class)
    private fun savePrivateImage(context: Context, bitmap: Bitmap, name: String): Uri {
        val image = File(context.filesDir, "$name.png")
        image.writeBitmap(bitmap)

        return FileProvider.getUriForFile(context, "com.realsoc.cropngrid.provider", image)
    }

    @Throws(IOException::class)
    private fun File.writeBitmap(bitmap: Bitmap) {
        try {
            FileOutputStream(this).use { fos ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                    throw IOException("Failed to save bitmap to $path.")
                }
            }
        } catch (e: Exception) {
            // Do not leave a truncated file behind
            delete()
            throw e
        }
    }
}