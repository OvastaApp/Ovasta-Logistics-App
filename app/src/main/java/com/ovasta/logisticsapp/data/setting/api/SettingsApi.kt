package com.ovasta.logisticsapp.data.setting.api

import retrofit2.http.POST


interface SettingsApi {
    @POST("logout")
    suspend fun logout()
}