package com.ovasta.logisticsapp.data.setting.data

import com.maxab.basemodule.core.setting.model.Country
import com.maxab.basemodule.core.setting.model.Language
import com.maxab.basemodule.core.setting.model.RemoteConfigModel
import com.ovasta.logisticsapp.data.User
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {

    suspend fun saveUserData(user: User)
    suspend fun getUseData(): User?

    suspend fun updateShiftStatus(isSignedIn: Boolean)

    fun observeShiftStatus(): Flow<Boolean>
    suspend fun logout()
    suspend fun clearUserData()

}