package com.ovasta.logisticsapp.presentation.home.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ovasta.logisticsapp.base.services.LocationTrackerService
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HomeRepository(
    private val homeRemoteDataSource: IHomeRemoteDataSource,
    val settingsRepository: ISettingsRepository
) : IHomeRepository {
    override suspend fun getAssignedTasks(
        userId: Int, districId: Int, userType: String
    ) = homeRemoteDataSource.getAssignedTasks(userId, districId, userType)

    override suspend fun startLocationTracking(context: Context) {
        withContext(Dispatchers.Main) {
            try {
                val intent = Intent(context, LocationTrackerService::class.java).apply {
                    action = LocationTrackerService.Action.START.name
                }
                context.startService(intent)
                settingsRepository.updateTrackingStatus(true)
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
                settingsRepository.updateTrackingStatus(false)
                Log.d("LocationTrackingRepository", "Location tracking stopped")
            } catch (ex: Exception) {
                Log.e("LocationTrackingRepository", "Error stopping location tracking", ex)
                throw ex
            }
        }
    }

    override suspend fun sendLocation(lat: Double, long: Double) {
        with(settingsRepository.getUseData()) {
            homeRemoteDataSource.logLocation(
                userId = this?.id ?: 0,
                districtId = this?.districId ?: 0,
                lat,
                long
            )
        }
    }

    override fun observeShiftStatus(): Flow<Boolean> {
        return settingsRepository.observeShiftStatus()
    }
}