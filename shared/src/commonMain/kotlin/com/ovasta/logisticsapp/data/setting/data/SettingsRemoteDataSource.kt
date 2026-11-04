package com.ovasta.logisticsapp.data.setting.data


class SettingsRemoteDataSource(private val settingsApi: SettingsApi) : ISettingsRemoteDataSource {

    override suspend fun logout() {
        return settingsApi.logout()
    }

    override suspend fun updateFcmToken(token: String) =
        settingsApi.updateFcmToken(FcmTokenRequest(token))

}