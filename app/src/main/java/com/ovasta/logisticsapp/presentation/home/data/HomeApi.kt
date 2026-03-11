package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HomeApi {
    @POST("change-partner-status")
    suspend fun changePartnerStatus(@Query("is_online") isOnline: Boolean? = false)

    @GET("get-partner-status")
    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>
}