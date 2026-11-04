package com.ovasta.logisticsapp.presentation.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateFeesRequest(
    @SerialName("delivery_price") val deliveryPrice: String
)