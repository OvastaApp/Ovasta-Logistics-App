package com.ovasta.logisticsapp.base.platform

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ovasta.logisticsapp.base.ext.compressImageToJpeg
import com.ovasta.logisticsapp.base.ext.createImageCaptureUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberImagePicker(maxBytes: Int, onImage: (PickedImage) -> Unit): ImagePicker {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnImage by rememberUpdatedState(onImage)
    // File the camera app writes the photo into; saveable because the camera app can push
    // this screen out of memory
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    fun deliver(uri: Uri) {
        scope.launch {
            val jpeg = runCatching {
                withContext(Dispatchers.IO) { context.compressImageToJpeg(uri, maxBytes = maxBytes) }
            }.onFailure { Log.w("ImagePicker", "Unreadable image: $uri", it) }.getOrNull()
            jpeg?.let { currentOnImage(PickedImage(it)) }
        }
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { image ->
            if (image != null) deliver(image)
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
            val uri = cameraImageUri
            if (saved && uri != null) deliver(uri)
        }

    return remember(galleryLauncher, cameraLauncher) {
        object : ImagePicker {
            override fun pickFromGallery() {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }

            override fun capturePhoto() {
                val captureUri = context.createImageCaptureUri()
                cameraImageUri = captureUri
                // No camera app on the device: nothing gets attached
                runCatching { cameraLauncher.launch(captureUri) }
            }
        }
    }
}
