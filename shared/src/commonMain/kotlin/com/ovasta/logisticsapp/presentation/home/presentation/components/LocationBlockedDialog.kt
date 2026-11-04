package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ovasta.logisticsapp.base.PlatformAction
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseDialog
import com.ovasta.logisticsapp.base.rememberPlatformActions
import com.ovasta.logisticsapp.base.services.LocationBlockReason
import com.ovasta.logisticsapp.base.services.LocationTrackingMonitor
import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.cancel
import com.ovasta.logisticsapp.shared.resources.ic_location
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** How often an open refusal re-checks its cause, so it updates or closes once fixed. */
private const val RECHECK_INTERVAL_MS = 2_000L

/**
 * Explains why an order action was refused ([LocationTrackingMonitor.blockedActionReason]) and
 * offers the fix: the app's settings, the location settings, or restarting tracking.
 */
@Composable
fun LocationBlockedDialogHost(onRestoreTracking: () -> Unit) {
    val monitor: LocationTrackingMonitor = koinInject()
    val blockReason by monitor.blockedActionReason.collectAsState()
    val platformActions = rememberPlatformActions()

    // A refusal doesn't outlive the logged-in screens, e.g. after logout
    DisposableEffect(Unit) {
        onDispose { monitor.dismissBlockedAction() }
    }

    blockReason?.let { reason ->
        LaunchedEffect(Unit) {
            while (true) {
                delay(RECHECK_INTERVAL_MS)
                monitor.recheckBlockedAction()
            }
        }

        BaseDialog(
            icon = painterResource(Res.drawable.ic_location),
            title = stringResource(reason.titleRes),
            message = stringResource(reason.messageRes),
            primaryButtonText = stringResource(reason.actionRes),
            secondaryButtonText = stringResource(Res.string.cancel),
            onPrimaryClick = {
                when (reason) {
                    LocationBlockReason.PERMISSION_DENIED -> platformActions(PlatformAction.OpenAppSettings)
                    LocationBlockReason.LOCATION_DISABLED -> platformActions(PlatformAction.OpenLocationSettings)
                    LocationBlockReason.TRACKING_OFF,
                    LocationBlockReason.LOGGING_NOT_WORKING -> onRestoreTracking()
                }
            },
            onSecondaryClick = monitor::dismissBlockedAction,
            onDismiss = monitor::dismissBlockedAction,
            dismissOnClickOutside = false
        )
    }
}
