package com.ovasta.logisticsapp.base.platform

import com.ovasta.logisticsapp.base.services.LocationTracker
import com.ovasta.logisticsapp.base.services.LocationTrackingMonitor
import com.ovasta.logisticsapp.presentation.home.data.DeviceLocation
import com.ovasta.logisticsapp.presentation.home.data.GeoPoint
import com.ovasta.logisticsapp.presentation.home.data.TrackingKeepAlive
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.CLActivityTypeAutomotiveNavigation
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.timeIntervalSinceDate
import platform.darwin.NSObject
import kotlin.coroutines.resume

private const val CURRENT_FIX_TIMEOUT_MS = 10_000L
// The monitor blocks order actions after 60 s without a fix; iOS sends none while the phone
// doesn't move (distanceFilter), so the last fix is passed on again after this long
private const val STATIONARY_REPEAT_MS = 30_000L
private const val MIN_DISTANCE_METERS = 10.0

// CLLocationManager is created and used on the main thread, whose run loop delivers its
// delegate callbacks. It holds its delegate weakly, so whoever sets one keeps a reference.

private fun CLLocationManager.isAuthorized(): Boolean =
    authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
            authorizationStatus == kCLAuthorizationStatusAuthorizedAlways

@OptIn(ExperimentalForeignApi::class)
private fun CLLocation.toGeoPoint(): GeoPoint =
    coordinate.useContents { GeoPoint(latitude, longitude) }

/** Forwards CLLocationManager callbacks to Kotlin lambdas. */
private class LocationDelegate(
    private val onLocation: (CLLocation) -> Unit,
    private val onError: (NSError) -> Unit = {},
) : NSObject(), CLLocationManagerDelegateProtocol {
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        (didUpdateLocations.lastOrNull() as? CLLocation)?.let(onLocation)
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onError(didFailWithError)
    }
}

/** The current fix, location services and the "When In Use" permission. */
class IosDeviceLocation : DeviceLocation {

    private val manager by lazy { CLLocationManager() }

    // Kept while a one-shot requestLocation() is running
    private var pendingDelegate: LocationDelegate? = null

    /**
     * Asks for "When In Use" the first time; later calls do nothing. Without asking, Settings
     * has no location switch for the app. Call on the main thread, e.g. when the UI starts.
     */
    fun requestPermission() {
        if (manager.authorizationStatus == kCLAuthorizationStatusNotDetermined) {
            manager.requestWhenInUseAuthorization()
        }
    }

    override suspend fun currentLocation(): GeoPoint? = withContext(Dispatchers.Main) {
        if (!manager.isAuthorized()) return@withContext null
        withTimeoutOrNull(CURRENT_FIX_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val delegate = LocationDelegate(
                    onLocation = { location ->
                        if (continuation.isActive) continuation.resume(location.toGeoPoint())
                    },
                    onError = { if (continuation.isActive) continuation.resume(null) }
                )
                pendingDelegate = delegate
                manager.delegate = delegate
                manager.desiredAccuracy = kCLLocationAccuracyBest
                manager.requestLocation()
                continuation.invokeOnCancellation { manager.stopUpdatingLocation() }
            }
        }.also {
            manager.delegate = null
            pendingDelegate = null
        }
    }

    override fun isLocationEnabled(): Boolean = CLLocationManager.locationServicesEnabled()

    override fun hasLocationPermission(): Boolean = manager.isAuthorized()
}

/**
 * Background location updates while the driver is online: the iOS counterpart of Android's
 * foreground LocationTrackerService. It feeds its fixes to [LocationTracker], like the service.
 * Start it while the app is in the foreground; iOS then keeps delivering fixes in the
 * background with "When In Use" permission, and shows the blue location indicator.
 */
class IosTrackingKeepAlive(
    // Lazy: LocationTracker needs IHomeRepository, which needs this
    private val tracker: Lazy<LocationTracker>,
    private val monitor: LocationTrackingMonitor,
) : TrackingKeepAlive {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var trackingJob: Job? = null

    override suspend fun start() {
        withContext(Dispatchers.Main) {
            trackingJob?.cancel()
            trackingJob = scope.launch { tracker.value.track(fixes()) }
        }
    }

    override suspend fun stop() {
        withContext(Dispatchers.Main) {
            trackingJob?.cancel()
            trackingJob = null
            monitor.onTrackingStopped()
        }
    }

    private fun fixes(): Flow<GeoPoint> = callbackFlow {
        val locationManager = CLLocationManager().apply {
            desiredAccuracy = kCLLocationAccuracyBest
            distanceFilter = MIN_DISTANCE_METERS
            activityType = CLActivityTypeAutomotiveNavigation
            allowsBackgroundLocationUpdates = true
            pausesLocationUpdatesAutomatically = false
            showsBackgroundLocationIndicator = true
        }
        var lastFix: CLLocation? = null
        val locationDelegate = LocationDelegate(onLocation = { location ->
            lastFix = location
            trySend(location.toGeoPoint())
        })
        locationManager.delegate = locationDelegate
        locationManager.startUpdatingLocation()

        val repeater = launch {
            while (true) {
                delay(STATIONARY_REPEAT_MS)
                val fix = lastFix ?: continue
                val ageMs = NSDate().timeIntervalSinceDate(fix.timestamp) * 1000
                if (ageMs >= STATIONARY_REPEAT_MS) trySend(fix.toGeoPoint())
            }
        }

        // Referencing the delegate here keeps it alive while the flow runs
        awaitClose {
            repeater.cancel()
            locationManager.stopUpdatingLocation()
            if (locationManager.delegate === locationDelegate) locationManager.delegate = null
        }
    }
}
