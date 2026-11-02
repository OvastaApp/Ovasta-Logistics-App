package com.ovasta.logisticsapp.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Root of the shared UI. For now it only proves the iOS app runs shared Compose code;
 * the Android app keeps its own UI until the screens move here (Phase 4 of the KMP plan).
 */
@Composable
fun App() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BasicText("Ovasta Logistics on ${platformName()}")
    }
}
