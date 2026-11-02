package com.ovasta.logisticsapp.data.setting.data


interface ISettingsRemoteDataSource {
    suspend fun logout()
    suspend fun updateFcmToken(token: String)
}