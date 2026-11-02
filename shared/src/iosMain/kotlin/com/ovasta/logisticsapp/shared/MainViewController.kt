package com.ovasta.logisticsapp.shared

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** Hosts the shared Compose UI; iosApp's ContentView wraps it for SwiftUI. */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
