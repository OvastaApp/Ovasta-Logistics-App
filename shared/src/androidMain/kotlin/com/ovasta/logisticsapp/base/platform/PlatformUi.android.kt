package com.ovasta.logisticsapp.base.platform

import android.os.Build
import android.os.SystemClock
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

internal actual fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()

@Composable
internal actual fun smallestScreenWidthDp(): Int = LocalConfiguration.current.smallestScreenWidthDp

@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        null
    }
