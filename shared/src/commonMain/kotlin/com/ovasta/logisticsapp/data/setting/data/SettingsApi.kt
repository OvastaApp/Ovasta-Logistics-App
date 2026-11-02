package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.data.remote.postJson
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class SettingsApi(private val client: HttpClient) {

    suspend fun logout() {
        client.get("logout")
    }

    suspend fun updateFcmToken(request: FcmTokenRequest) {
        client.postJson("fcm-token", request)
    }
}
