package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.data.User
import kotlinx.coroutines.flow.Flow

interface ISettingsLocalDataSource {
    suspend fun getUseData(): User?
    suspend fun clearUserData()
    suspend fun saveUserData(user: User)
    suspend fun updateTrackingStatus(isTracking: Boolean)
    fun observeShiftStatus(): Flow<Boolean>
}