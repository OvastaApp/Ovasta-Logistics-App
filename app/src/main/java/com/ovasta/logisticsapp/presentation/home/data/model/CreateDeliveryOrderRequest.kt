package com.ovasta.logisticsapp.presentation.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDeliveryOrderRequest(
    @SerialName("delivery_price") val deliveryPrice: Double,
    @SerialName("receiver_mobile") val receiverMobile: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("note") val note: String? = null,
)
