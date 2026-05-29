package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.AcceptOrderRequest
import com.ovasta.logisticsapp.presentation.home.data.model.AssignedDeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    @POST("change-partner-status")
    suspend fun changePartnerStatus(@Body changeStatusRequest: ChangeStatusRequest)

    @GET("get-partner-status")
    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>

    @GET("partner-statistics")
    suspend fun getPartnerStatistics(
        @Query("month") month: Int,
        @Query("year") year: Int,
    ): ApiResponse<PartnerStatistics>

    @GET("delivery-orders")
    suspend fun getDeliveryOrders(): ApiResponse<List<AssignedDeliveryTask>>

    @POST("delivery-orders/{id}/accept")
    suspend fun acceptDeliveryOrder(@Path("id") orderId: Int): ApiResponse<Unit>

    @POST("delivery-orders/{id}/pick")
    suspend fun pickDeliveryOrder(@Path("id") orderId: Int): ApiResponse<Unit>

    @POST("delivery-orders/{id}/deliver")
    suspend fun deliverDeliveryOrder(@Path("id") orderId: Int): ApiResponse<Unit>
}