package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.data.User
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {

    suspend fun saveUserData(user: User)
    suspend fun getUseData(): User?
    suspend fun logout()
    suspend fun clearUserData()

}