package com.ovasta.logisticsapp.presentation.home.data

import android.content.Context
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    suspend fun getAssignedTasks(
        userId: Int,
        branchId: Int,
        userType: String
    ): Flow<List<HomeTask>>

    /**
     * Starts location tracking by launching the LocationTrackerService
     * @param context Android application context
     */
    suspend fun startLocationTracking(context: Context)

    /**
     * Stops location tracking by stopping the LocationTrackerService
     * @param context Android application context
     */
    suspend fun stopLocationTracking(context: Context)

    /**
     * Checks if location tracking is currently active
     * @return Boolean indicating if tracking is active
     */

    suspend fun sendLocation(lat: Double, long: Double)

    fun observeShiftStatus(): Flow<Boolean>
}