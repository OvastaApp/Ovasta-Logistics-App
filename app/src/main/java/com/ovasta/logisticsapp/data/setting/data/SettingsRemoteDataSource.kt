package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.data.setting.api.SettingsApi

class SettingsRemoteDataSource(private val settingsApi: SettingsApi) : ISettingsRemoteDataSource {

    override suspend fun logout() {
        return settingsApi.logout()
    }

}