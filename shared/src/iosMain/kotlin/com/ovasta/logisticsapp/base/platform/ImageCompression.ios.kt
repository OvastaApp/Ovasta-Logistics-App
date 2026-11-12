@file:OptIn(ExperimentalForeignApi::class)

package com.ovasta.logisticsapp.base.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIRectFill
import platform.posix.memcpy
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// Same values as Android's compressImageToJpeg (androidMain base/ext/ImageExt.kt)
private const val MAX_SIDE_PX = 1_600
private const val JPEG_QUALITY = 80
// Below this, JPEG artifacts start blurring receipt text, so shrink the image instead
private const val MIN_JPEG_QUALITY = 60
private const val JPEG_QUALITY_STEP = 10
private const val DOWNSCALE_FACTOR = 0.75

/**
 * Turns [image] upright, scales it down so its longest side is at most [maxSide] px and encodes
 * it as JPEG at [quality]. If that is still larger than [maxBytes], the quality is lowered (down
 * to [MIN_JPEG_QUALITY]) and then the image is shrunk until it fits, as on Android.
 */
internal fun compressImageToJpeg(
    image: UIImage,
    maxSide: Int = MAX_SIDE_PX,
    quality: Int = JPEG_QUALITY,
    maxBytes: Int = MAX_UPLOAD_IMAGE_BYTES,
): ByteArray {
    // UIImage.size is already in display orientation; times scale gives pixels
    val (width, height) = image.size.useContents { width * image.scale to height * image.scale }
    require(width > 0 && height > 0) { "Image has no pixels" }

    val scale = min(1.0, maxSide / max(width, height))
    var targetWidth = (width * scale).roundToInt().coerceAtLeast(1)
    var targetHeight = (height * scale).roundToInt().coerceAtLeast(1)
    var rendered = image.redrawn(targetWidth, targetHeight)

    // Too big for the upload limit: lower the JPEG quality first, then the resolution
    var currentQuality = quality
    var bytes = rendered.toJpeg(currentQuality)
    while (bytes.size > maxBytes) {
        if (currentQuality > MIN_JPEG_QUALITY) {
            currentQuality = max(MIN_JPEG_QUALITY, currentQuality - JPEG_QUALITY_STEP)
        } else {
            targetWidth = (targetWidth * DOWNSCALE_FACTOR).toInt().coerceAtLeast(1)
            targetHeight = (targetHeight * DOWNSCALE_FACTOR).toInt().coerceAtLeast(1)
            rendered = image.redrawn(targetWidth, targetHeight)
        }
        bytes = rendered.toJpeg(currentQuality)
    }
    return bytes
}

/**
 * Draws this image at [width] x [height] pixels. Drawing applies the EXIF orientation, so the
 * result is upright; transparent areas become white, since JPEG has no alpha.
 */
private fun UIImage.redrawn(width: Int, height: Int): UIImage {
    val format = UIGraphicsImageRendererFormat().apply {
        scale = 1.0
        opaque = true
    }
    val size = CGSizeMake(width.toDouble(), height.toDouble())
    val rect = CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble())
    return UIGraphicsImageRenderer(size = size, format = format).imageWithActions {
        UIColor.whiteColor.setFill()
        UIRectFill(rect)
        drawInRect(rect)
    }
}

private fun UIImage.toJpeg(quality: Int): ByteArray =
    UIImageJPEGRepresentation(this, quality / 100.0)?.toByteArray() ?: error("JPEG encoding failed")

private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).apply {
        usePinned { memcpy(it.addressOf(0), bytes, length) }
    }
}
