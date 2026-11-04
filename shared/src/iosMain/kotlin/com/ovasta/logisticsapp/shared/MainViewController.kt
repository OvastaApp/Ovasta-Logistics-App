package com.ovasta.logisticsapp.shared

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.ComposeUIViewController
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.base.local.applyAppLanguage
import com.ovasta.logisticsapp.base.local.savedAppLanguage
import com.ovasta.logisticsapp.base.platform.IosDeviceLocation
import com.ovasta.logisticsapp.data.remote.IosNetworkErrorMessages
import platform.UIKit.UIViewController

/**
 * Hosts the shared [App]; iosApp's ContentView wraps it for SwiftUI and builds a new one after
 * a language change. Needs [initKoin] first.
 */
fun MainViewController(): UIViewController {
    // The saved language, not the device's, like LocaleHelper on Android
    val language = savedAppLanguage()
    applyAppLanguage(language)
    koin().get<IosNetworkErrorMessages>().reload()
    // Android asks at every launch (MainActivity); iOS shows the prompt only once
    koin().get<IosDeviceLocation>().requestPermission()

    val layoutDirection = if (language == LANGUAGE_AR_ISO) LayoutDirection.Rtl else LayoutDirection.Ltr
    return ComposeUIViewController {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            App()
        }
    }
}
