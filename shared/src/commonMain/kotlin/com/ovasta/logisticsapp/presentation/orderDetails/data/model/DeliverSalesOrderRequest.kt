package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliverSalesOrderRequest(
    @SerialName("collected_amount") val collectedAmount: Double
)
