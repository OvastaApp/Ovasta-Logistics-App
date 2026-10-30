package com.ovasta.logisticsapp.base.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.ovasta.logisticsapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Why order actions are blocked, from the most to the least fundamental cause. */
enum class LocationBlockReason(
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
    @StringRes val actionRes: Int
) {
    /** The app may not read the device location. */
    PERMISSION_DENIED(
        R.string.location_blocked_permission_title,
        R.string.location_blocked_permission_message,
        R.string.location_blocked_permission_action
    ),

    /** Location services are turned off on the device. */
    LOCATION_DISABLED(
        R.string.location_blocked_disabled_title,
        R.string.location_blocked_disabled_message,
        R.string.location_blocked_disabled_action
    ),

    /** [LocationTrackerService] isn't running, e.g. the driver is offline. */
    TRACKING_OFF(
        R.string.location_blocked_tracking_title,
        R.string.location_blocked_tracking_message,
        R.string.location_blocked_tracking_action
    ),

    /** [LocationTrackerService] runs but gets no location fixes or can't log them. */
    LOGGING_NOT_WORKING(
        R.string.location_blocked_logging_title,
        R.string.location_blocked_logging_message,
        R.string.try_again
    )
}

/**
 * Decides whether the driver may act on orders: only while the app can read their location and
 * [LocationTrackerService] is running and logging it. The service reports its progress here;
 * order actions call [ensureLocationTracking] first, which raises [blockedActionReason] for the
 * dialog that explains a refusal.
 */
class LocationTrackingMonitor(private val context: Context) {

    private data class TrackerState(
        val isRunning: Boolean = false,
        /** [SystemClock.elapsedRealtime] of the last (re)start, so a fresh start isn't stale yet. */
        val startedAt: Long = 0L,
        /** [SystemClock.elapsedRealtime] of the last location fix. */
        val lastFixAt: Long = 0L,
        val lastLogFailed: Boolean = false
    )

    private val trackerState = MutableStateFlow(TrackerState())

    private val _blockedActionReason = MutableStateFlow<LocationBlockReason?>(null)

    /** Why the last order action was refused, while that still holds; null when nothing was refused. */
    val blockedActionReason: StateFlow<LocationBlockReason?> = _blockedActionReason.asStateFlow()

    /** Why order actions are blocked right now, or null when they are allowed. */
    fun currentBlockReason(): LocationBlockReason? {
        val state = trackerState.value
        val lastSignOfLife = maxOf(state.startedAt, state.lastFixAt)
        return when {
            !hasLocationPermission() -> LocationBlockReason.PERMISSION_DENIED
            !isLocationEnabled() -> LocationBlockReason.LOCATION_DISABLED
            !state.isRunning -> LocationBlockReason.TRACKING_OFF
            state.lastLogFailed || SystemClock.elapsedRealtime() - lastSignOfLife > STALE_AFTER_MS ->
                LocationBlockReason.LOGGING_NOT_WORKING

            else -> null
        }
    }

    /**
     * Returns whether an order action may run now. When it may not, tells the driver why through
     * [blockedActionReason].
     */
    fun ensureLocationTracking(): Boolean {
        val reason = currentBlockReason() ?: return true
        _blockedActionReason.value = reason
        return false
    }

    /** Re-checks a refusal: it follows the current cause, or clears once that is fixed. */
    fun recheckBlockedAction() {
        _blockedActionReason.update { shown -> shown?.let { currentBlockReason() } }
    }

    fun dismissBlockedAction() {
        _blockedActionReason.value = null
    }

    fun onTrackingStarted() {
        trackerState.value = TrackerState(isRunning = true, startedAt = SystemClock.elapsedRealtime())
    }

    fun onTrackingStopped() {
        trackerState.update { it.copy(isRunning = false) }
    }

    fun onLocationReceived() {
        trackerState.update { it.copy(lastFixAt = SystemClock.elapsedRealtime()) }
    }

    fun onLocationLogged(success: Boolean) {
        trackerState.update { it.copy(lastLogFailed = !success) }
    }

    private fun hasLocationPermission(): Boolean =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    companion object {
        /** No location fix for this long means tracking has stalled (fixes arrive every ~10s). */
        private const val STALE_AFTER_MS = 60_000L
    }
}
