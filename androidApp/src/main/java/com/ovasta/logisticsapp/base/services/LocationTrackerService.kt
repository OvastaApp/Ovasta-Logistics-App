package com.ovasta.logisticsapp.base.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationTrackerService() : Service(), KoinComponent {
    private val homeRepository: IHomeRepository by inject()
    private val settingsRepository: ISettingsRepository by inject()
    private val locationTrackingMonitor: LocationTrackingMonitor by inject()
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private var trackingJob: kotlinx.coroutines.Job? = null
    private var lastLoggedLocation: android.location.Location? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(
        intent: Intent?, flags: Int, startId: Int
    ): Int {
        // For Android 12+ (API 31+), we must call startForeground() immediately
        // when the service is started, before any other operations
        when (intent?.action) {
            Action.START.name -> start()
            Action.STOP.name -> stop()
            else -> {
                // Service restarted by system after being killed — resume tracking
                start()
            }
        }

        return START_STICKY
    }

    private fun start() {
        // Create and show notification FIRST (before any other operations)
        // Android 12+ requires startForeground() to be called within 5 seconds
        val notification = NotificationCompat
            .Builder(this, LOCATION_CHANNEL)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Location Tracker")
            .setContentText("Initializing location tracking...")
            .setStyle(NotificationCompat.BigTextStyle())
            .build()

        startForeground(NOTIFICATION_ID, notification)

        // Don't start tracking if location services are disabled
        val androidLocationManager = getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        if (!androidLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) &&
            !androidLocationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
            Log.w("LocationTracker", "Location services disabled, stopping service")
            stop()
            return
        }

        // Cancel any existing tracking job to prevent duplicate collections
        trackingJob?.cancel()
        // Log the first fix after every (re)start, so a restart re-checks that logging works
        lastLoggedLocation = null
        locationTrackingMonitor.onTrackingStarted()

        val locationManager: LocationManager by inject()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        trackingJob = scope.launch {
            locationManager.trackLocation().collect { location ->
                locationTrackingMonitor.onLocationReceived()
                val latitude = location.latitude
                val longitude = location.longitude

                val shouldLog = lastLoggedLocation?.let { last ->
                    location.distanceTo(last) >= 10f
                } ?: true

                if (shouldLog) {
                    val logged = try {
                        // A write the server doesn't confirm in time (e.g. while offline) failed
                        withTimeoutOrNull(LOG_TIMEOUT_MS) {
                            homeRepository.sendLocation(lat = latitude, long = longitude)
                        } != null
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        Log.e("LocationTracker", "Failed to send location", e)
                        false
                    }
                    if (logged) lastLoggedLocation = location
                    locationTrackingMonitor.onLocationLogged(logged)
                }

                // Update notification with current location
                val updatedNotification = NotificationCompat
                    .Builder(this@LocationTrackerService, LOCATION_CHANNEL)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Location Tracker")
                    .setContentText("Location: $latitude / $longitude")
                    .setStyle(NotificationCompat.BigTextStyle())
                    .build()

                notificationManager.notify(NOTIFICATION_ID, updatedNotification)
            }
        }

    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        locationTrackingMonitor.onTrackingStopped()
    }

    enum class Action {
        START, STOP
    }

    companion object {
        const val LOCATION_CHANNEL = "location_channel"
        private const val NOTIFICATION_ID = 1
        private const val LOG_TIMEOUT_MS = 15_000L
    }
}