package com.ovasta.logisticsapp.base.ext

import androidx.compose.runtime.Composable

// Not NSLog("%@", message): Kotlin/Native passes a String through C varargs as a Kotlin object,
// not an NSString, and NSLog hung on it, freezing the main thread on every toast.
@Composable
internal actual fun rememberPlatformToaster(): (String) -> Unit = IosToaster::show
