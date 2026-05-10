package com.ovasta.logisticsapp.presentation.home.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ovasta.logisticsapp.base.services.LocationTrackerService
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(
    private val homeFirebaseRemoteDataSource: IHomeFirebaseRemoteDataSource,
    private val homeServerRemoteDataSource: IHomeServerRemoteDataSource,
    val settingsRepository: ISettingsRepository
) : IHomeRepository {
    override suspend fun getAssignedTasks(
        userId: Int, districtId: Int
    ) = homeFirebaseRemoteDataSource.getAssignedTasks(userId, districtId)

    override suspend fun getAvailableSellerOrders(
        userId: Int,
        districtId: Int
    ) = homeFirebaseRemoteDataSource.getAvailableSellerOrders(
        userId = userId,
        districtId = districtId
    )

    override suspend fun startLocationTracking(context: Context) {
        withContext(Dispatchers.Main) {
            try {
                val intent = Intent(context, LocationTrackerService::class.java).apply {
                    action = LocationTrackerService.Action.START.name
                }
                context.startService(intent)
                Log.d("LocationTrackingRepository", "Location tracking started")
            } catch (ex: Exception) {
                Log.e("LocationTrackingRepository", "Error starting location tracking", ex)
                throw ex
            }
        }
    }

    override suspend fun stopLocationTracking(context: Context) {
        withContext(Dispatchers.Main) {
            try {
                val intent = Intent(context, LocationTrackerService::class.java).apply {
                    action = LocationTrackerService.Action.STOP.name
                }
                context.stopService(intent)
                Log.d("LocationTrackingRepository", "Location tracking stopped")
            } catch (ex: Exception) {
                Log.e("LocationTrackingRepository", "Error stopping location tracking", ex)
                throw ex
            }
        }
    }

    override suspend fun sendLocation(lat: Double, long: Double) {
        with(settingsRepository.getUseData()) {
            homeFirebaseRemoteDataSource.logLocation(
                userId = this?.deliveryId ?: 0,
                districtId = this?.districtId ?: 0,
                lat,
                long
            )
        }
    }

    override suspend fun changePartnerStatus(isOnline: Boolean) =
        homeServerRemoteDataSource.changePartnerStatus(isOnline = isOnline)

    override suspend fun getPartnerStatus() = homeServerRemoteDataSource.getPartnerStatus()

    override suspend fun getPartnerStatistics(
        month: Int,
        year: Int,
    ) = homeServerRemoteDataSource.getPartnerStatistics(month, year)

    override suspend fun changeOrderStatus(orderId: Int, status: OrderSteps) =
        homeServerRemoteDataSource.changeOrderStatus(orderId, status)

}