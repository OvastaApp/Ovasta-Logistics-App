package com.ovasta.logisticsapp.base.services

import com.ovasta.logisticsapp.presentation.home.data.GeoPoint
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Turns the platform's location fixes into the driver's logged position (Firestore
 * online_drivers/{id}) and reports tracking health to [LocationTrackingMonitor]. The platform
 * owns the fixes and keeping the app alive (LocationTrackerService on Android).
 */
class LocationTracker(
    private val homeRepository: IHomeRepository,
    private val monitor: LocationTrackingMonitor,
) {

    /**
     * Collects [fixes] until cancelled. The first fix after every start is always logged, so a
     * restart re-checks that logging works; after that only moves of [MIN_LOG_DISTANCE_METERS]
     * or more. A write the server doesn't confirm within [LOG_TIMEOUT_MS] (e.g. while offline)
     * counts as failed and doesn't move the last logged point.
     *
     * @param onFix called with every fix, e.g. to show it in a notification
     */
    suspend fun track(fixes: Flow<GeoPoint>, onFix: (GeoPoint) -> Unit = {}) {
        monitor.onTrackingStarted()
        var lastLogged: GeoPoint? = null
        fixes.collect { fix ->
            monitor.onLocationReceived()
            val shouldLog = lastLogged?.let { distanceMeters(it, fix) >= MIN_LOG_DISTANCE_METERS } ?: true
            if (shouldLog) {
                val logged = try {
                    withTimeoutOrNull(LOG_TIMEOUT_MS) {
                        homeRepository.sendLocation(lat = fix.latitude, long = fix.longitude)
                    } != null
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    false
                }
                if (logged) lastLogged = fix
                monitor.onLocationLogged(logged)
            }
            onFix(fix)
        }
    }

    companion object {
        const val MIN_LOG_DISTANCE_METERS = 10.0
        const val LOG_TIMEOUT_MS = 15_000L
    }
}

private const val EARTH_RADIUS_METERS = 6_371_008.8

/** Great-circle (haversine) distance between two points, in meters. */
internal fun distanceMeters(a: GeoPoint, b: GeoPoint): Double {
    val lat1 = a.latitude.toRadians()
    val lat2 = b.latitude.toRadians()
    val dLat = lat2 - lat1
    val dLng = (b.longitude - a.longitude).toRadians()
    val h = sin(dLat / 2) * sin(dLat / 2) + cos(lat1) * cos(lat2) * sin(dLng / 2) * sin(dLng / 2)
    return 2 * EARTH_RADIUS_METERS * asin(sqrt(h))
}

private fun Double.toRadians(): Double = this * kotlin.math.PI / 180
