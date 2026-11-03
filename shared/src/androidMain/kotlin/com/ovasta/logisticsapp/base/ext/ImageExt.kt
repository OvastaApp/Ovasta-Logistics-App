package com.ovasta.logisticsapp.base.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max
import kotlin.math.min

/** Cache sub-folder for camera captures; must match `res/xml/file_paths.xml`. */
private const val CAPTURED_IMAGES_DIR = "images"

/** Largest image file we upload: 200 KB (200,000 bytes, so it fits either KB definition). */
const val MAX_UPLOAD_IMAGE_BYTES = 200_000

// Below this, JPEG artifacts start blurring receipt text, so shrink the image instead
private const val MIN_JPEG_QUALITY = 60
private const val JPEG_QUALITY_STEP = 10
private const val DOWNSCALE_FACTOR = 0.75f

/**
 * Creates an empty file for the camera app to write a photo into and returns its
 * FileProvider uri (authority `<applicationId>.fileprovider`, declared in the manifest).
 * Earlier captures are deleted first — they have already been uploaded or abandoned.
 */
fun Context.createImageCaptureUri(): Uri {
    val dir = File(cacheDir, CAPTURED_IMAGES_DIR).apply { mkdirs() }
    dir.listFiles()?.forEach { it.delete() }
    val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
}

/**
 * Reads the image at [uri], turns it upright using its EXIF rotation, scales it down so its
 * longest side is at most [maxSide] px and re-encodes it as JPEG at [quality]. If the file is
 * still larger than [maxBytes], the quality is lowered (down to [MIN_JPEG_QUALITY]) and then the
 * image is shrunk until it fits. Keeps camera photos quick to upload over mobile data and under
 * server upload limits.
 */
fun Context.compressImageToJpeg(
    uri: Uri,
    maxSide: Int = 1_600,
    quality: Int = 80,
    maxBytes: Int = MAX_UPLOAD_IMAGE_BYTES,
): ByteArray {
    // Read the size first, then decode at the largest power-of-two sample that keeps >= maxSide
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    val longestSide = max(bounds.outWidth, bounds.outHeight)
    require(longestSide > 0) { "Unreadable image: $uri" }

    var sampleSize = 1
    while (longestSide / (sampleSize * 2) >= maxSide) sampleSize *= 2

    val decoded = contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sampleSize })
    } ?: error("Unreadable image: $uri")

    val rotation = contentResolver.openInputStream(uri)?.use { ExifInterface(it).rotationDegrees } ?: 0
    val scale = min(1f, maxSide.toFloat() / max(decoded.width, decoded.height))
    val matrix = Matrix().apply {
        postRotate(rotation.toFloat())
        postScale(scale, scale)
    }
    var bitmap = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)

    // Too big for the upload limit: lower the JPEG quality first, then the resolution
    var currentQuality = quality
    var bytes = bitmap.toJpeg(currentQuality)
    while (bytes.size > maxBytes) {
        if (currentQuality > MIN_JPEG_QUALITY) {
            currentQuality = max(MIN_JPEG_QUALITY, currentQuality - JPEG_QUALITY_STEP)
        } else {
            val smaller = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * DOWNSCALE_FACTOR).toInt().coerceAtLeast(1),
                (bitmap.height * DOWNSCALE_FACTOR).toInt().coerceAtLeast(1),
                true
            )
            if (bitmap !== decoded) bitmap.recycle()
            bitmap = smaller
        }
        bytes = bitmap.toJpeg(currentQuality)
    }

    if (bitmap !== decoded) bitmap.recycle()
    decoded.recycle()
    return bytes
}

private fun Bitmap.toJpeg(quality: Int): ByteArray =
    ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, quality, stream)
        stream.toByteArray()
    }
