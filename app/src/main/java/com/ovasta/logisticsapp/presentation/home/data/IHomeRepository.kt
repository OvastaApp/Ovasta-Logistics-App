package com.ovasta.logisticsapp.presentation.home.data

import android.content.Context
import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    suspend fun getAssignedTasks(
        userId: Int,
        districtId: Int
    ): Flow<List<HomeTask>>

    suspend fun getAvailableSellerOrders(
        userId: Int, districtId: Int
    ): Flow<List<SellerTask>>

    /**
     * Starts location tracking by launching the LocationTrackerService
     * @param context Android application context
     */
    suspend

    fun startLocationTracking(context: Context)

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

    suspend fun changePartnerStatus(isOnline: Boolean)

    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>

    suspend fun getPartnerStatistics(
        month: Int,
        year: Int,
    ): ApiResponse<PartnerStatistics>

    suspend fun changeOrderStatus(orderId: Int, status: OrderSteps)

}