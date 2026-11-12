package com.ovasta.logisticsapp.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/** Posted on a language change; iosApp's ContentView recreates the Compose view for it. */
const val LANGUAGE_CHANGED_NOTIFICATION = "com.ovasta.logisticsapp.LanguageChanged"

// The "not available" toasts come with the iOS in-app toast (Phase 5); until then a missing
// number or point does nothing
@Composable
internal actual fun rememberPlatformActionPerformer(): (PlatformAction) -> Unit = remember {
    { action ->
        when (action) {
            PlatformAction.LanguageChanged ->
                NSNotificationCenter.defaultCenter.postNotificationName(LANGUAGE_CHANGED_NOTIFICATION, null)
            is PlatformAction.Call ->
                action.phone?.takeIf { it.isNotEmpty() }?.let { open("tel:$it") }
            is PlatformAction.OpenMaps -> {
                val lat = action.latitude
                val lng = action.longitude
                if (lat != null && lng != null) {
                    // Google Maps if installed, else Apple Maps
                    if (!open("comgooglemaps://?daddr=$lat,$lng&directionsmode=driving")) {
                        open("http://maps.apple.com/?daddr=$lat,$lng")
                    }
                }
            }
            is PlatformAction.OpenWhatsApp ->
                action.phone.orEmpty().filter { it.isDigit() }.takeIf { it.isNotEmpty() }
                    ?.let { open("https://wa.me/$it") }
            // iOS has no direct link to the location settings; the app's page has its permission
            PlatformAction.OpenAppSettings, PlatformAction.OpenLocationSettings ->
                open(UIApplicationOpenSettingsURLString)
        }
    }
}

/** Opens [url] if some app can; returns whether one could. */
private fun open(url: String): Boolean {
    val nsUrl = NSURL.URLWithString(url) ?: return false
    val app = UIApplication.sharedApplication
    if (!app.canOpenURL(nsUrl)) return false
    app.openURL(nsUrl, options = emptyMap<Any?, Any>(), completionHandler = null)
    return true
}

// iOS can't wake the screen or show over the lock screen; it only stops auto-lock
@Composable
actual fun WakeScreenEffect() {
    DisposableEffect(Unit) {
        UIApplication.sharedApplication.idleTimerDisabled = true
        onDispose { UIApplication.sharedApplication.idleTimerDisabled = false }
    }
}
