package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import com.google.gson.annotations.SerializedName

data class DeliverSalesOrderRequest(
    @SerializedName("collected_amount") val collectedAmount: Double
)
