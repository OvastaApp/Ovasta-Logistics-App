package com.ovasta.logisticsapp.base.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSLog

// The PHPicker / camera implementation comes with the iOS platform work (Phase 5); until then
// the buttons do nothing on iOS
@Composable
actual fun rememberImagePicker(maxBytes: Int, onImage: (PickedImage) -> Unit): ImagePicker = remember {
    object : ImagePicker {
        override fun pickFromGallery() = NSLog("ImagePicker: gallery is not implemented on iOS yet")
        override fun capturePhoto() = NSLog("ImagePicker: camera is not implemented on iOS yet")
    }
}
