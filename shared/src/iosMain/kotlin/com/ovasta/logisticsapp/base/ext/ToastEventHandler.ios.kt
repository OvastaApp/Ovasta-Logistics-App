package com.ovasta.logisticsapp.base.ext

import androidx.compose.runtime.Composable
import platform.Foundation.NSLog

// iOS has no system toast; an in-app toast (ToastMsg) comes with the iOS platform work (Phase 5)
@Composable
internal actual fun rememberPlatformToaster(): (String) -> Unit = { message -> NSLog("Toast: %@", message) }
