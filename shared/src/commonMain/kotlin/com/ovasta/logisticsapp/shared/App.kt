package com.ovasta.logisticsapp.shared

import androidx.compose.runtime.Composable
import com.ovasta.logisticsapp.presentation.nav.AppNavHost
import com.ovasta.logisticsapp.ui.theme.OvastaLogisticsAppTheme

/**
 * The whole app UI. Koin must be started first with the shared modules (networkModule,
 * sessionModule, firebaseModule, trackingModule, viewModelModule) and the platform's bindings
 * (see KoinModulesTest).
 */
@Composable
fun App() {
    OvastaLogisticsAppTheme {
        AppNavHost()
    }
}
