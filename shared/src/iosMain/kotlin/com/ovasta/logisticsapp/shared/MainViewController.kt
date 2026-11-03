package com.ovasta.logisticsapp.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Hosts the shared Compose UI; iosApp's ContentView wraps it for SwiftUI.
 *
 * Shows a placeholder until the iOS Koin modules exist (Phase 5: location tracking, alarm,
 * session store, push token...); then it starts Koin and shows [App].
 */
fun MainViewController(): UIViewController = ComposeUIViewController {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BasicText("Ovasta Logistics on ${platformName()}")
    }
}
