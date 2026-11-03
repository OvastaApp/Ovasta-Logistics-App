package com.ovasta.logisticsapp.presentation.home.di

import android.content.Context
import com.ovasta.logisticsapp.base.services.LocationManager
import com.ovasta.logisticsapp.base.services.LocationTrackingGuard
import com.ovasta.logisticsapp.base.services.LocationTrackingMonitor
import com.ovasta.logisticsapp.presentation.home.data.DeviceLocation
import com.ovasta.logisticsapp.presentation.home.data.GeoPoint
import com.ovasta.logisticsapp.presentation.home.data.HomeRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.SeenTaskStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {
    single<IHomeRepository> { HomeRepository(get(), get(), get(), androidContext()) }
    single { LocationManager(androidContext()) }
    single { LocationTrackingMonitor(androidContext()) } bind LocationTrackingGuard::class
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
