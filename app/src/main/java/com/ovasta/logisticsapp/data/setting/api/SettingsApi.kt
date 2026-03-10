package com.ovasta.logisticsapp.data.setting.api

import retrofit2.http.GET


interface SettingsApi {
    @GET("logout")
    suspend fun logout()
}