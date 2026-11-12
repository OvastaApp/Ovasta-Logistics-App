package com.ovasta.logisticsapp.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.ovasta.logisticsapp.base.ext.IosToaster
import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.calls_not_available
import com.ovasta.logisticsapp.shared.resources.maps_error_pkg
import com.ovasta.logisticsapp.shared.resources.not_available
import com.ovasta.logisticsapp.shared.resources.phone_number_not_available
import com.ovasta.logisticsapp.shared.resources.whatsapp_is_not_installed_on_the_device
import org.jetbrains.compose.resources.stringResource
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/** Posted on a language change; iosApp's ContentView recreates the Compose view for it. */
const val LANGUAGE_CHANGED_NOTIFICATION = "com.ovasta.logisticsapp.LanguageChanged"

// Tells the driver what's missing with the same toasts as Android
@Composable
internal actual fun rememberPlatformActionPerformer(): (PlatformAction) -> Unit {
    val notAvailable = stringResource(Res.string.not_available)
    val mapsError = stringResource(Res.string.maps_error_pkg)
    val phoneNotAvailable = stringResource(Res.string.phone_number_not_available)
    val whatsAppMissing = stringResource(Res.string.whatsapp_is_not_installed_on_the_device)
    val callsNotAvailable = stringResource(Res.string.calls_not_available)
    return remember(notAvailable, mapsError, phoneNotAvailable, whatsAppMissing, callsNotAvailable) {
        { action ->
            when (action) {
                PlatformAction.LanguageChanged ->
                    NSNotificationCenter.defaultCenter.postNotificationName(LANGUAGE_CHANGED_NOTIFICATION, null)
                is PlatformAction.Call -> {
                    val number = dialableNumber(action.phone)
                    if (number == null) {
                        IosToaster.show(notAvailable)
                    } else {
                        // Not through open(): canOpenURL refuses tel: unless Info.plist lists it,
                        // and openURL doesn't need that. iOS asks "Call <number>?" first, like
                        // Android's ACTION_DIAL. Fails where there's no Phone app (the simulator).
                        openReportingFailure("tel:$number") { IosToaster.show(callsNotAvailable) }
                    }
                }
                is PlatformAction.OpenMaps -> {
                    val lat = action.latitude
                    val lng = action.longitude
                    when {
                        lat == null || lng == null -> IosToaster.show(notAvailable)
                        // Google Maps if installed, else Apple Maps
                        !open("comgooglemaps://?daddr=$lat,$lng&directionsmode=driving") &&
                                !open("http://maps.apple.com/?daddr=$lat,$lng") -> IosToaster.show(mapsError)
                    }
                }
                is PlatformAction.OpenWhatsApp -> {
                    val digits = action.phone.orEmpty().filter { it.isDigit() }
                    when {
                        digits.isEmpty() -> IosToaster.show(phoneNotAvailable)
                        !open("https://wa.me/$digits") -> IosToaster.show(whatsAppMissing)
                    }
                }
                // iOS has no direct link to the location settings; the app's page has its permission
                PlatformAction.OpenAppSettings, PlatformAction.OpenLocationSettings -> {
                    open(UIApplicationOpenSettingsURLString)
                }
            }
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

/** Opens [url] without asking canOpenURL first; [onFailure] runs if no app opened it. */
private fun openReportingFailure(url: String, onFailure: () -> Unit) {
    val nsUrl = NSURL.URLWithString(url) ?: return onFailure()
    UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any>()) { opened ->
        if (!opened) onFailure()
    }
}

/**
 * [phone] as digits with an optional leading +, the form a tel: URL accepts: spaces, dashes and
 * brackets would make the URL invalid. Null if no digits are left.
 */
private fun dialableNumber(phone: String?): String? {
    val raw = phone.orEmpty().trim()
    val digits = raw.filter { it.isDigit() }
    if (digits.isEmpty()) return null
    return if (raw.startsWith("+")) "+$digits" else digits
}

// iOS can't wake the screen or show over the lock screen; it only stops auto-lock
@Composable
actual fun WakeScreenEffect() {
    DisposableEffect(Unit) {
        UIApplication.sharedApplication.idleTimerDisabled = true
        onDispose { UIApplication.sharedApplication.idleTimerDisabled = false }
    }
}
