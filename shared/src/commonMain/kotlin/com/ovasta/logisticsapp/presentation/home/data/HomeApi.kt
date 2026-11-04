package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.remote.patchJson
import com.ovasta.logisticsapp.data.remote.postJson
import com.ovasta.logisticsapp.presentation.home.data.model.AssignedDeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.logisticsapp.presentation.home.data.model.CreateDeliveryOrderRequest
import com.ovasta.logisticsapp.presentation.home.data.model.HomeData
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import com.ovasta.logisticsapp.presentation.home.data.model.UpdateFeesRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.serialization.json.JsonElement

class HomeApi(private val client: HttpClient) {

    suspend fun changePartnerStatus(changeStatusRequest: ChangeStatusRequest) {
        client.postJson("change-partner-status", changeStatusRequest)
    }

    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus> =
        client.get("get-partner-status").body()

    suspend fun getPartnerStatistics(month: Int, year: Int): ApiResponse<PartnerStatistics> =
        client.get("partner-statistics") {
            parameter("month", month)
            parameter("year", year)
        }.body()

    /** The courier's account and wallet state, including whether they are an outsource agent. */
    suspend fun getHome(): ApiResponse<HomeData?> = client.get("home").body()

    suspend fun getDeliveryOrders(): ApiResponse<List<AssignedDeliveryTask>> =
        client.get("delivery-orders").body()

    /** Marks an assigned (sales) order as started when the agent first opens it. */
    suspend fun startSalesOrder(orderId: Int): ApiResponse<JsonElement> =
        client.post("sales-orders/$orderId/confirm-receive").body()

    suspend fun acceptDeliveryOrder(orderId: Int): ApiResponse<JsonElement> =
        client.post("delivery-orders/$orderId/accept").body()

    suspend fun pickDeliveryOrder(orderId: Int): ApiResponse<JsonElement> =
        client.post("delivery-orders/$orderId/pick").body()

    suspend fun deliverDeliveryOrder(orderId: Int): ApiResponse<JsonElement> =
        client.post("delivery-orders/$orderId/deliver").body()

    suspend fun createDeliveryOrder(createDeliveryOrderRequest: CreateDeliveryOrderRequest) {
        client.postJson("delivery-orders", createDeliveryOrderRequest)
    }

    suspend fun updateDeliveryOrderFees(orderId: Int, updateFeesRequest: UpdateFeesRequest) {
        client.patchJson("delivery-orders/$orderId/delivery-fee", updateFeesRequest)
    }
}
