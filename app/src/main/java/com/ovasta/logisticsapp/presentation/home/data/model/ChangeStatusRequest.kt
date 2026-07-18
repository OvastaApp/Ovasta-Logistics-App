package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class ChangeStatusRequest(
    @SerializedName("online") val isOnline: Boolean,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("long") val long: Double? = null
)