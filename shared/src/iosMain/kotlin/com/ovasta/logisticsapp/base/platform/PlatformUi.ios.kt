package com.ovasta.logisticsapp.base.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import platform.Foundation.NSProcessInfo
import kotlin.math.min

internal actual fun elapsedRealtimeMillis(): Long =
    (NSProcessInfo.processInfo.systemUptime * 1000).toLong()

@Composable
internal actual fun smallestScreenWidthDp(): Int {
    val size = LocalWindowInfo.current.containerSize
    return with(LocalDensity.current) { min(size.width, size.height).toDp().value.toInt() }
}

// iOS has no wallpaper colors; the theme uses its own scheme
@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null
