package com.ovasta.logisticsapp.base.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.uikit.LocalUIViewController
import com.ovasta.logisticsapp.base.ext.IosToaster
import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.camera_not_available
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.darwin.NSObject

@Composable
actual fun rememberImagePicker(maxBytes: Int, onImage: (PickedImage) -> Unit): ImagePicker {
    val hostController = LocalUIViewController.current
    val scope = rememberCoroutineScope()
    val currentOnImage by rememberUpdatedState(onImage)
    val cameraNotAvailable = stringResource(Res.string.camera_not_available)

    return remember(hostController, scope, maxBytes, cameraNotAvailable) {
        IosImagePicker(
            hostController = hostController,
            scope = scope,
            maxBytes = maxBytes,
            cameraNotAvailable = cameraNotAvailable,
            onImage = { currentOnImage(it) },
        )
    }
}

private class IosImagePicker(
    private val hostController: UIViewController,
    private val scope: CoroutineScope,
    private val maxBytes: Int,
    private val cameraNotAvailable: String,
    private val onImage: (PickedImage) -> Unit,
) : ImagePicker {
    // UIKit holds picker delegates weakly; this keeps the open picker's delegate alive
    private var activeDelegate: NSObject? = null

    override fun pickFromGallery() {
        // PHPicker runs out of process, so it needs no photo library permission
        val configuration = PHPickerConfiguration().apply {
            filter = PHPickerFilter.imagesFilter
            selectionLimit = 1
        }
        val picker = PHPickerViewController(configuration)
        val delegate = GalleryDelegate { result ->
            activeDelegate = null
            result?.let(::loadGalleryImage)
        }
        activeDelegate = delegate
        picker.delegate = delegate
        topController().presentViewController(picker, animated = true, completion = null)
    }

    override fun capturePhoto() {
        // No camera on the simulator; presenting the picker without one throws
        val camera = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        if (!UIImagePickerController.isSourceTypeAvailable(camera)) {
            IosToaster.show(cameraNotAvailable)
            return
        }
        // The picker asks for camera access itself (NSCameraUsageDescription in Info.plist)
        val picker = UIImagePickerController().apply { sourceType = camera }
        val delegate = CameraDelegate { image ->
            activeDelegate = null
            image?.let { deliver { it } }
        }
        activeDelegate = delegate
        picker.delegate = delegate
        topController().presentViewController(picker, animated = true, completion = null)
    }

    private fun loadGalleryImage(result: PHPickerResult) {
        // Downloads the photo from iCloud if needed; the handler runs on a background queue
        result.itemProvider.loadDataRepresentationForTypeIdentifier(UTTypeImage.identifier) { data, error ->
            if (data == null) {
                println("ImagePicker: unreadable gallery image: ${error?.localizedDescription}")
                return@loadDataRepresentationForTypeIdentifier
            }
            deliver { UIImage(data = data) }
        }
    }

    /** Compresses the image [readImage] returns off the main thread, then hands it to [onImage]. */
    private fun deliver(readImage: () -> UIImage?) {
        scope.launch {
            val jpeg = withContext(Dispatchers.Default) {
                runCatching { readImage()?.let { compressImageToJpeg(it, maxBytes = maxBytes) } }
                    .onFailure { println("ImagePicker: unreadable image: ${it.message}") }
                    .getOrNull()
            }
            jpeg?.let { onImage(PickedImage(it)) }
        }
    }

    /** The screen's controller, or whatever it's currently presenting (pickers need the top one). */
    private fun topController(): UIViewController {
        var top = hostController
        while (true) top = top.presentedViewController ?: return top
    }
}

private class GalleryDelegate(
    private val onFinished: (PHPickerResult?) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        // Empty when the user cancels
        onFinished(didFinishPicking.firstOrNull() as? PHPickerResult)
    }
}

private class CameraDelegate(
    private val onFinished: (UIImage?) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)
        onFinished(didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        onFinished(null)
    }
}
