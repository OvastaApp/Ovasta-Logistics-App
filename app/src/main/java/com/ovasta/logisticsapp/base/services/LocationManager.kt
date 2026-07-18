package com.ovasta.logisticsapp.base.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
class LocationManager(
    context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? =
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    // Fallback to last known location if a fresh fix isn't available
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { last -> continuation.resume(last) }
                        .addOnFailureListener { continuation.resume(null) }
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
            continuation.invokeOnCancellation { cancellationTokenSource.cancel() }
        }

    fun trackLocation(): Flow<Location> {
        return callbackFlow {
            val locationCallback = locationCallback { location ->
                launch {
                    send(location)
                }
            }

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000
            ).setMinUpdateIntervalMillis(7000)
                .build()
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }

        }
    }

    private fun locationCallback(
        onResult: (location: Location) -> Unit
    ): LocationCallback {

        return object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    onResult(location)
                }
            }
        }
    }

}