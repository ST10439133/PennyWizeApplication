package com.CFC.pennywizeapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts CameraX ImageProxy to Android Bitmap
 */
fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    return try {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        val matrix = Matrix()
        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Compresses bitmap to reduce file size
 */
fun compressBitmap(bitmap: Bitmap, maxSizeKB: Int = 500, quality: Int = 85): Bitmap {
    var currentQuality = quality
    var stream = ByteArrayOutputStream()

    while (currentQuality > 20) {
        stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, stream)

        if (stream.size() <= maxSizeKB * 1024) {
            break
        }

        currentQuality -= 5
    }

    return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
}

/**
 * Resizes bitmap to max dimensions while maintaining aspect ratio
 */
fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val scale = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height, 1.0f)

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

/**
 * Saves bitmap to INTERNAL storage (private to app) - BEST for RoomDB
 * Returns the file path as String to store in database
 */
fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): String? {
    return try {
        val receiptsDir = File(context.filesDir, "receipts")
        if (!receiptsDir.exists()) {
            receiptsDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "receipt_${timeStamp}_${System.currentTimeMillis()}.jpg"
        val file = File(receiptsDir, fileName)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        }

        println("ImageUtils: Image saved to internal storage: ${file.absolutePath}")
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Saves bitmap to device gallery using MediaStore (for user to view later)
 * Returns the URI of the saved image, or null if save failed
 */
fun saveImageToGallery(context: Context, bitmap: Bitmap): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val displayName = "PennyWize_${timeStamp}.jpg"

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/PennyWize")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
        }
        uri
    } else {
        @Suppress("DEPRECATION")
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val pennyWizeDir = File(picturesDir, "PennyWize")

        if (!pennyWizeDir.exists()) {
            pennyWizeDir.mkdirs()
        }

        val imageFile = File(pennyWizeDir, displayName)
        return try {
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            Uri.fromFile(imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * Load image from internal storage by file path
 */
fun loadImageFromInternalStorage(path: String): Bitmap? {
    return try {
        val file = File(path)
        if (file.exists()) {
            BitmapFactory.decodeFile(path)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Delete image from internal storage
 */
fun deleteImageFromInternalStorage(path: String): Boolean {
    return try {
        val file = File(path)
        file.delete()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}