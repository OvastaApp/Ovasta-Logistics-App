package com.ovasta.logisticsapp.data.setting.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SettingsApi {
    @GET("logout")
    suspend fun logout()

    @POST("fcm-token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest)

}