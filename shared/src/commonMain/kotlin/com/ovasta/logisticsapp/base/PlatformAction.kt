package com.ovasta.logisticsapp.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/** Things a ViewModel asks the platform to do; see [PlatformActionHandler]. */
sealed interface PlatformAction {
    /** The app language changed: reload every screen in it and its layout direction. */
    data object LanguageChanged : PlatformAction

    /** Opens the dialer with [phone]; tells the user when there is no number. */
    data class Call(val phone: String?) : PlatformAction

    /** Starts navigation to a point in a maps app; tells the user when there is no point. */
    data class OpenMaps(val latitude: Double?, val longitude: Double?) : PlatformAction

    /** Opens a WhatsApp chat with [phone]; tells the user when there is no number. */
    data class OpenWhatsApp(val phone: String?) : PlatformAction

    /** Opens this app's page in the system settings, e.g. to grant the location permission. */
    data object OpenAppSettings : PlatformAction

    /** Opens the device's location settings (the app's settings where there are none). */
    data object OpenLocationSettings : PlatformAction
}

/** Performs [PlatformAction]s directly, for screens that don't route them through a ViewModel. */
@Composable
fun rememberPlatformActions(): (PlatformAction) -> Unit = rememberPlatformActionPerformer()

/** Keeps the screen on, and on Android wakes it and shows over the lock screen, while shown. */
@Composable
expect fun WakeScreenEffect()

/** Performs the ViewModel's [PlatformAction]s. */
@Composable
fun PlatformActionHandler(viewModel: BaseViewModel) {
    val perform = rememberPlatformActionPerformer()
    LaunchedEffect(viewModel.platformAction) {
        viewModel.platformAction.collect { perform(it) }
    }
}

@Composable
internal expect fun rememberPlatformActionPerformer(): (PlatformAction) -> Unit
