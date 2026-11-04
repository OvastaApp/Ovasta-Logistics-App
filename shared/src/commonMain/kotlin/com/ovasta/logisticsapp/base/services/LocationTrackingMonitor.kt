package com.ovasta.logisticsapp.base.services

import com.ovasta.logisticsapp.base.platform.elapsedRealtimeMillis
import com.ovasta.logisticsapp.presentation.home.data.DeviceLocation
import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.location_blocked_disabled_action
import com.ovasta.logisticsapp.shared.resources.location_blocked_disabled_message
import com.ovasta.logisticsapp.shared.resources.location_blocked_disabled_title
import com.ovasta.logisticsapp.shared.resources.location_blocked_logging_message
import com.ovasta.logisticsapp.shared.resources.location_blocked_logging_title
import com.ovasta.logisticsapp.shared.resources.location_blocked_permission_action
import com.ovasta.logisticsapp.shared.resources.location_blocked_permission_message
import com.ovasta.logisticsapp.shared.resources.location_blocked_permission_title
import com.ovasta.logisticsapp.shared.resources.location_blocked_tracking_action
import com.ovasta.logisticsapp.shared.resources.location_blocked_tracking_message
import com.ovasta.logisticsapp.shared.resources.location_blocked_tracking_title
import com.ovasta.logisticsapp.shared.resources.try_again
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource

/** Why order actions are blocked, from the most to the least fundamental cause. */
enum class LocationBlockReason(
    val titleRes: StringResource,
    val messageRes: StringResource,
    val actionRes: StringResource
) {
    /** The app may not read the device location. */
    PERMISSION_DENIED(
        Res.string.location_blocked_permission_title,
        Res.string.location_blocked_permission_message,
        Res.string.location_blocked_permission_action
    ),

    /** Location services are turned off on the device. */
    LOCATION_DISABLED(
        Res.string.location_blocked_disabled_title,
        Res.string.location_blocked_disabled_message,
        Res.string.location_blocked_disabled_action
    ),

    /** Tracking isn't running, e.g. the driver is offline. */
    TRACKING_OFF(
        Res.string.location_blocked_tracking_title,
        Res.string.location_blocked_tracking_message,
        Res.string.location_blocked_tracking_action
    ),

    /** Tracking runs but gets no location fixes or can't log them. */
    LOGGING_NOT_WORKING(
        Res.string.location_blocked_logging_title,
        Res.string.location_blocked_logging_message,
        Res.string.try_again
    )
}

/**
 * Decides whether the driver may act on orders: only while the app can read their location and
 * tracking is running and logging it. The platform's tracker (LocationTrackerService on Android)
 * reports its progress here; order actions call [ensureLocationTracking] first, which raises
 * [blockedActionReason] for the dialog that explains a refusal.
 */
class LocationTrackingMonitor(private val deviceLocation: DeviceLocation) : LocationTrackingGuard {

    private data class TrackerState(
        val isRunning: Boolean = false,
        /** [elapsedRealtimeMillis] of the last (re)start, so a fresh start isn't stale yet. */
        val startedAt: Long = 0L,
        /** [elapsedRealtimeMillis] of the last location fix. */
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
            !deviceLocation.hasLocationPermission() -> LocationBlockReason.PERMISSION_DENIED
            !deviceLocation.isLocationEnabled() -> LocationBlockReason.LOCATION_DISABLED
            !state.isRunning -> LocationBlockReason.TRACKING_OFF
            state.lastLogFailed || elapsedRealtimeMillis() - lastSignOfLife > STALE_AFTER_MS ->
                LocationBlockReason.LOGGING_NOT_WORKING
            else -> null
        }
    }

    /**
     * Returns whether an order action may run now. When it may not, tells the driver why through
     * [blockedActionReason].
     */
    override fun ensureLocationTracking(): Boolean {
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
        trackerState.value = TrackerState(isRunning = true, startedAt = elapsedRealtimeMillis())
    }

    fun onTrackingStopped() {
        trackerState.update { it.copy(isRunning = false) }
    }

    fun onLocationReceived() {
        trackerState.update { it.copy(lastFixAt = elapsedRealtimeMillis()) }
    }

    fun onLocationLogged(success: Boolean) {
        trackerState.update { it.copy(lastLogFailed = !success) }
    }

    companion object {
        /** No location fix for this long means tracking has stalled (fixes arrive every ~10s). */
        internal const val STALE_AFTER_MS = 60_000L
    }
}
