package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class AcceptOrderRequest(
    @SerializedName("id") val id: Int
)