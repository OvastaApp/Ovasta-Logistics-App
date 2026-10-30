package com.ovasta.logisticsapp.presentation.home.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseDialog
import com.ovasta.logisticsapp.base.services.LocationBlockReason
import com.ovasta.logisticsapp.base.services.LocationTrackingMonitor
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

private const val RECHECK_INTERVAL_MS = 2_000L

/**
 * Explains why an order action was refused ([LocationTrackingMonitor.blockedActionReason]) and
 * offers the fix: the system settings for permission / location problems, otherwise
 * [onRestoreTracking]. While open it follows the current cause and closes once that is fixed.
 */
@Composable
fun LocationBlockedDialogHost(onRestoreTracking: () -> Unit) {
    val monitor: LocationTrackingMonitor = koinInject()
    val blockReason by monitor.blockedActionReason.collectAsState()
    val context = LocalContext.current

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
            icon = painterResource(R.drawable.ic_location),
            title = stringResource(reason.titleRes),
            message = stringResource(reason.messageRes),
            primaryButtonText = stringResource(reason.actionRes),
            secondaryButtonText = stringResource(R.string.cancel),
            onPrimaryClick = {
                when (reason) {
                    LocationBlockReason.PERMISSION_DENIED -> context.openSettings(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )

                    LocationBlockReason.LOCATION_DISABLED ->
                        context.openSettings(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

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

/** Some devices don't ship every settings screen; the dialog then simply stays open. */
private fun Context.openSettings(intent: Intent) {
    runCatching { startActivity(intent) }
}
