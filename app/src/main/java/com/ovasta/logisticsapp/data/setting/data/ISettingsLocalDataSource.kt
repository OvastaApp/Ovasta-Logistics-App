package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.data.User

interface ISettingsLocalDataSource {
    suspend fun getUseData(): User?
    suspend fun clearUserData()
    suspend fun saveUserData(user: User)
}