package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.data.setting.model.Country
import com.ovasta.logisticsapp.data.setting.model.RemoteConfigModel

interface ISettingsRemoteDataSource {
    suspend fun logout()
    suspend fun updateFcmToken(token: String)
}