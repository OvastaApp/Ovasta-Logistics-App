package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class UpdateFeesRequest(
    @SerializedName("delivery_price") val deliveryPrice: String
)