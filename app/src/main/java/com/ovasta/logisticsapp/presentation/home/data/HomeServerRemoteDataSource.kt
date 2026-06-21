package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.logisticsapp.presentation.home.data.model.CreateDeliveryOrderRequest
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.home.data.model.UpdateFeesRequest

class HomeServerRemoteDataSource(private val homeApi: HomeApi) : IHomeServerRemoteDataSource {

    override suspend fun changePartnerStatus(isOnline: Boolean) {
        val changeStatusRequest = ChangeStatusRequest(isOnline = isOnline)
        homeApi.changePartnerStatus(changeStatusRequest)
    }

    override suspend fun getPartnerStatus() = homeApi.getPartnerStatus()

    override suspend fun getPartnerStatistics(
        month: Int,
        year: Int,
    ) = homeApi.getPartnerStatistics(month, year)

    override suspend fun changeOrderStatus(orderId: Int, status: OrderSteps): ApiResponse<Unit> {
        return when (status) {
            is OrderSteps.Assigned -> homeApi.acceptDeliveryOrder(orderId)
            is OrderSteps.Picked -> homeApi.pickDeliveryOrder(orderId)
            else -> homeApi.deliverDeliveryOrder(orderId)
        }
    }

    override suspend fun getAssignedDeliveryOrders() = homeApi.getDeliveryOrders().data
    override suspend fun createDeliveryOrder(
        deliveryPrice: Double,
        receiverMobile: String?,
        toAddress: String?,
        note: String?,
    ) {
        val request = CreateDeliveryOrderRequest(
            deliveryPrice = deliveryPrice,
            receiverMobile = receiverMobile?.takeIf { it.isNotBlank() },
            toAddress = toAddress?.takeIf { it.isNotBlank() },
            note = note?.takeIf { it.isNotBlank() },
        )
        homeApi.createDeliveryOrder(request)
    }

    override suspend fun updateDeliveryOrderFees(orderId: Int, deliveryFees: Double) {
        val price = if (deliveryFees % 1.0 == 0.0) {
            deliveryFees.toLong().toString()
        } else {
            deliveryFees.toString()
        }
        val request = UpdateFeesRequest(deliveryPrice = price)
        homeApi.updateDeliveryOrderFees(orderId, request)
    }
}