package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class CreateDeliveryOrderRequest(
    @SerializedName("delivery_price") val deliveryPrice: Double,
    @SerializedName("receiver_mobile") val receiverMobile: String? = null,
    @SerializedName("to_address") val toAddress: String? = null,
    @SerializedName("note") val note: String? = null,
)
