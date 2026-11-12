package com.ovasta.logisticsapp.base.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.presentation.home.data.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationTrackerService() : Service(), KoinComponent {
    private val locationTracker: LocationTracker by inject()
    private val locationTrackingMonitor: LocationTrackingMonitor by inject()
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private var trackingJob: kotlinx.coroutines.Job? = null

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
            .setContentTitle(getString(R.string.location_tracking_title))
            .setContentText(getString(R.string.location_tracking_text))
            .setOngoing(true)
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

        val locationManager: LocationManager by inject()

        // LocationTracker filters, logs and reports each fix; the service only keeps it running
        trackingJob = scope.launch {
            locationTracker.track(
                fixes = locationManager.trackLocation().map { GeoPoint(it.latitude, it.longitude) }
            )
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
    }
}