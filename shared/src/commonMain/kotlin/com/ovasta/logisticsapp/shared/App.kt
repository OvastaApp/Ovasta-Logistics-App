package com.ovasta.logisticsapp.shared

import androidx.compose.runtime.Composable
import com.ovasta.logisticsapp.presentation.nav.AppNavHost
import com.ovasta.logisticsapp.ui.theme.OvastaLogisticsAppTheme

/**
 * The whole app UI. Koin must be started first with the shared modules (networkModule,
 * sessionModule, firebaseModule, viewModelModule) and the platform's bindings.
 *
 * @param locationBlockedDialog explains order actions refused while the location isn't
 * tracked; Android passes its LocationBlockedDialogHost until it moves here (Phase 5)
 */
@Composable
fun App(locationBlockedDialog: @Composable (onRestoreTracking: () -> Unit) -> Unit = {}) {
    OvastaLogisticsAppTheme {
        AppNavHost(locationBlockedDialog = locationBlockedDialog)
    }
}
