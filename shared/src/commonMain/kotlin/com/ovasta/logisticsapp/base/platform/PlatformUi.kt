package com.ovasta.logisticsapp.base.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** Milliseconds since the device booted, like Android's `SystemClock.elapsedRealtime()`. */
internal expect fun elapsedRealtimeMillis(): Long

/** The screen's smallest width in dp, which the sdp/ssp scaling is based on. */
@Composable
internal expect fun smallestScreenWidthDp(): Int

/** The wallpaper-based color scheme where the platform has one (Android 12+), else null. */
@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?
