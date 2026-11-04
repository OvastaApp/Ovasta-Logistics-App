package com.ovasta.logisticsapp.base.services

/** Stops the driver's location tracking, e.g. on logout. */
fun interface LocationTrackingSwitch {
    suspend fun stopLocationTracking()
}
