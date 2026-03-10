package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.data.User
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val settingsRemoteDataSource: ISettingsRemoteDataSource,
    private val settingsLocalDataSource: ISettingsLocalDataSource
) : ISettingsRepository {
    override suspend fun saveUserData(user: User) {
        settingsLocalDataSource.saveUserData(user)
    }

    override suspend fun getUseData(): User? {
        return settingsLocalDataSource.getUseData()
    }

    override suspend fun updateTrackingStatus(isTracking: Boolean) {
        settingsLocalDataSource.updateTrackingStatus(isTracking = isTracking)
    }

    override fun observeShiftStatus(): Flow<Boolean> =
        settingsLocalDataSource.observeShiftStatus()

    override suspend fun logout() = settingsRemoteDataSource.logout()

    override suspend fun clearUserData() {
        settingsLocalDataSource.clearUserData()
    }
}