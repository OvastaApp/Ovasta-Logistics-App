package com.ovasta.logisticsapp.presentation.home.data

// What Home needs from the platform; the Android app implements these (see HomeModule)

/** The looping sound that announces new delivery orders while Home is open. */
interface OrderAlarm {
    fun startAlarm()
    fun stopAlarm()
}

/** A position on the map. */
data class GeoPoint(val latitude: Double, val longitude: Double)

/** The device's location services. */
interface DeviceLocation {
    /** The current position, or null when it can't be read. */
    suspend fun currentLocation(): GeoPoint?

    /** Whether location (GPS or network) is turned on. */
    fun isLocationEnabled(): Boolean
}

/** Delivery orders the courier has already been alerted about, kept across app restarts. */
interface SeenTaskStore {
    fun load(): MutableSet<Int>
    fun save(ids: Set<Int>)
}
