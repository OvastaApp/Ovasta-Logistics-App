package com.ovasta.logisticsapp.presentation.home.data

import android.content.Context
import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IHomeRepository {
    suspend fun getAssignedTasks(
        userId: Int,
        districId: Int,
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

    suspend fun sendLocation( lat: Double, long: Double)

    fun observeShiftStatus(): Flow<Boolean>

    suspend fun changePartnerStatus(isOnline: Boolean? = false)

    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>

}