package com.ovasta.logisticsapp.base.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver

/** Default size limit for uploaded photos (order images, receipts). */
const val MAX_UPLOAD_IMAGE_BYTES = 200_000

/** A photo the user picked or took, already compressed to JPEG for upload. */
class PickedImage(val jpeg: ByteArray) {
    companion object {
        /** Keeps a picked image across the screen being recreated (e.g. while the camera is open). */
        val Saver: Saver<PickedImage?, ByteArray> = Saver(
            save = { it?.jpeg },
            restore = { PickedImage(it) }
        )
    }
}

/** Opens the gallery or the camera; the photo arrives in [rememberImagePicker]'s callback. */
interface ImagePicker {
    fun pickFromGallery()
    fun capturePhoto()
}

/**
 * An [ImagePicker] whose photos are rotated upright, scaled down and compressed to JPEG under
 * [maxBytes] before [onImage] gets them. If no camera app is available or the image can't be
 * read, nothing is delivered.
 */
@Composable
expect fun rememberImagePicker(maxBytes: Int, onImage: (PickedImage) -> Unit): ImagePicker
