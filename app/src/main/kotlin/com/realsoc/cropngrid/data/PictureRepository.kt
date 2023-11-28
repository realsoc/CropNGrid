package com.realsoc.cropngrid.data

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Objects


interface PictureRepository {
    suspend fun saveImage(
        context: Application,
        bitmap: Bitmap,
        name: String,
        sharable: Boolean = false
    ): Uri?
}
class PictureRepositoryImpl : PictureRepository {
    override suspend fun saveImage(
        context: Application, bitmap: Bitmap, name: String,
        sharable: Boolean
    ):
            Uri? {
        return if (SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                if (sharable) {
                    savePublicImageInAndroidApi29AndAbove(context.contentResolver, bitmap, name)
                } else {
                    savePrivateImage(context, bitmap, name)
                }
            } catch (e: Exception) {

                println(e)
                null
            }
        } else {
            try {
                if (sharable) {
                    savePublicImageInAndroidApi28AndBelow(bitmap, name)
                } else {
                    savePrivateImage(context, bitmap, name)
                }
            } catch (e: Exception) {
                // Todo : what to do here
                println(e)
                null
            }
        }
    }

    @Throws(IOException::class)
    private fun savePublicImageInAndroidApi28AndBelow(bitmap: Bitmap, name: String): Uri {
        val fos: OutputStream
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val image = File(imagesDir, "$name.png")
        fos = FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        Objects.requireNonNull<OutputStream>(fos).close()

        return Uri.fromFile(image)
    }

    @Throws(IOException::class)
    private fun savePublicImageInAndroidApi29AndAbove(resolver: ContentResolver, bitmap: Bitmap, name: String): Uri {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
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
            uri
        } catch (e: IOException) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            throw e
        }
    }

    private fun savePrivateImage(context: Context, bitmap: Bitmap, name: String): Uri {
        val fos: OutputStream
        val image = File(context.filesDir, "$name.png")
        fos = FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        Objects.requireNonNull<OutputStream>(fos).close()

        return FileProvider.getUriForFile(context, "com.realsoc.cropngrid.provider", image)
    }
}