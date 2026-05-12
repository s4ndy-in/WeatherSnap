package com.example.weathersnap.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class CompressionResult(
    val originalPath: String,
    val compressedPath: String,
    val originalSizeKb: Long,
    val compressedSizeKb: Long
)

suspend fun compressImage(
    originalPath: String,
    outputDir: File,
    quality: Int = 50
): CompressionResult = withContext(Dispatchers.IO) {

    val originalFile = File(originalPath)
    val originalSizeKb = originalFile.length() / 1024

    val bitmap = BitmapFactory.decodeFile(originalPath)

    val compressedFile = File(outputDir, "compressed_${System.currentTimeMillis()}.jpg")
    FileOutputStream(compressedFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
    }
    bitmap.recycle()

    val compressedSizeKb = compressedFile.length() / 1024

    CompressionResult(
        originalPath = originalPath,
        compressedPath = compressedFile.absolutePath,
        originalSizeKb = originalSizeKb,
        compressedSizeKb = compressedSizeKb
    )
}