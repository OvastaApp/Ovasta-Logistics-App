package com.ovasta.logisticsapp.presentation.home.di

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.ovasta.logisticsapp.base.services.LocationManager
import com.ovasta.logisticsapp.base.services.LocationTrackerService
import com.ovasta.logisticsapp.presentation.home.data.DeviceLocation
import com.ovasta.logisticsapp.presentation.home.data.GeoPoint
import com.ovasta.logisticsapp.presentation.home.data.TrackingKeepAlive
import com.ovasta.logisticsapp.presentation.home.data.SeenTaskStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val homeModule = module {
    single<TrackingKeepAlive> { ServiceTrackingKeepAlive(androidContext()) }
    single { LocationManager(androidContext()) }
    single<DeviceLocation> { AndroidDeviceLocation(androidContext(), get()) }
    single<SeenTaskStore> { SharedPreferencesSeenTaskStore(androidContext()) }
}

/** The fused location provider for the current fix; GPS or network for "location on". */
private class AndroidDeviceLocation(
    private val context: Context,
    private val locationManager: LocationManager,
) : DeviceLocation {
    override suspend fun currentLocation(): GeoPoint? =
        locationManager.getCurrentLocation()?.let { GeoPoint(it.latitude, it.longitude) }

    override fun isLocationEnabled(): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    override fun hasLocationPermission(): Boolean =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}

/** The same SharedPreferences file and key as before the move, so seen orders carry over. */
private class SharedPreferencesSeenTaskStore(context: Context) : SeenTaskStore {
    private val prefs = context.getSharedPreferences("delivery_alerts", Context.MODE_PRIVATE)

    override fun load(): MutableSet<Int> =
        prefs.getStringSet("seen_task_ids", emptySet())!!.mapTo(mutableSetOf()) { it.toInt() }

    override fun save(ids: Set<Int>) {
        prefs.edit().putStringSet("seen_task_ids", ids.map { it.toString() }.toSet()).apply()
    }
}

/** Starts and stops the foreground LocationTrackerService, which feeds LocationTracker. */
private class ServiceTrackingKeepAlive(private val context: Context) : TrackingKeepAlive {

    override suspend fun start() {
        withContext(Dispatchers.Main) {
            val intent = Intent(context, LocationTrackerService::class.java)
                .setAction(LocationTrackerService.Action.START.name)
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (ex: Exception) {
                // Android 12+ refuses to start a foreground service from the background
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ex is ForegroundServiceStartNotAllowedException
                ) {
                    throw IllegalStateException(
                        "Location tracking can only be started when the app is visible. " +
                                "Please ensure the app is in the foreground and try again.",
                        ex
                    )
                }
                throw ex
            }
        }
    }

    override suspend fun stop() {
        withContext(Dispatchers.Main) {
            context.stopService(
                Intent(context, LocationTrackerService::class.java)
                    .setAction(LocationTrackerService.Action.STOP.name)
            )
        }
    }
}
