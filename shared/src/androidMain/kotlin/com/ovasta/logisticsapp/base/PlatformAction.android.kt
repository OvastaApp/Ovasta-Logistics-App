package com.ovasta.logisticsapp.base

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.maps_error_pkg
import com.ovasta.logisticsapp.shared.resources.not_available
import com.ovasta.logisticsapp.shared.resources.phone_number_not_available
import com.ovasta.logisticsapp.shared.resources.whatsapp_is_not_installed_on_the_device
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun rememberPlatformActionPerformer(): (PlatformAction) -> Unit {
    val context = LocalContext.current
    val messages = ContactMessages(
        notAvailable = stringResource(Res.string.not_available),
        mapsError = stringResource(Res.string.maps_error_pkg),
        phoneNotAvailable = stringResource(Res.string.phone_number_not_available),
        whatsAppMissing = stringResource(Res.string.whatsapp_is_not_installed_on_the_device),
    )
    return remember(context, messages) {
        { action ->
            when (action) {
                // Recreating the Activity reloads every screen in the new language and direction
                PlatformAction.LanguageChanged -> context.findActivity()?.recreate()
                is PlatformAction.Call -> context.makePhoneCall(action.phone, messages)
                is PlatformAction.OpenMaps ->
                    context.navigateTo(action.latitude, action.longitude, messages)
                is PlatformAction.OpenWhatsApp -> context.openWhatsApp(action.phone, messages)
                // Some devices don't ship every settings screen; nothing happens then
                PlatformAction.OpenAppSettings -> runCatching {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }
                PlatformAction.OpenLocationSettings -> runCatching {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }
        }
    }
}

@Composable
actual fun WakeScreenEffect() {
    val activity = LocalContext.current.findActivity()
    DisposableEffect(activity) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity?.setTurnScreenOn(true)
            activity?.setShowWhenLocked(true)
        } else {
            @Suppress("DEPRECATION")
            activity?.window?.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                activity?.setTurnScreenOn(false)
                activity?.setShowWhenLocked(false)
            } else {
                @Suppress("DEPRECATION")
                activity?.window?.clearFlags(
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                )
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private data class ContactMessages(
    val notAvailable: String,
    val mapsError: String,
    val phoneNotAvailable: String,
    val whatsAppMissing: String,
)

private fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

private fun Context.makePhoneCall(mobile: String?, messages: ContactMessages) {
    if (mobile.isNullOrEmpty()) {
        toast(messages.notAvailable)
        return
    }
    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile")))
}

/** Google Maps navigation, else any geo app, else the browser. */
private fun Context.navigateTo(latitude: Double?, longitude: Double?, messages: ContactMessages) {
    if (latitude == null || longitude == null) {
        toast(messages.notAvailable)
        return
    }
    val attempts = listOf(
        Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$latitude,$longitude&mode=d"))
            .setPackage("com.google.android.apps.maps"),
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")),
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")),
    )
    if (attempts.none { runCatching { startActivity(it) }.isSuccess }) toast(messages.mapsError)
}

/** wa.me with any app, else WhatsApp, else WhatsApp Business. */
private fun Context.openWhatsApp(phoneNumber: String?, messages: ContactMessages) {
    val cleanedNumber = phoneNumber.orEmpty().filter { it.isDigit() }
    if (cleanedNumber.isEmpty()) {
        toast(messages.phoneNotAvailable)
        return
    }
    val uri = Uri.parse("https://wa.me/$cleanedNumber")
    val attempts = listOf(
        Intent(Intent.ACTION_VIEW, uri),
        Intent(Intent.ACTION_VIEW, uri).setPackage("com.whatsapp"),
        Intent(Intent.ACTION_VIEW, uri).setPackage("com.whatsapp.w4b"),
    )
    if (attempts.none { runCatching { startActivity(it) }.isSuccess }) toast(messages.whatsAppMissing)
}
