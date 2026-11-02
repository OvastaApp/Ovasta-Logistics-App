package com.ovasta.logisticsapp.presentation.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangeStatusRequest(
    @SerialName("online") val isOnline: Boolean,
    @SerialName("lat") val lat: Double? = null,
    @SerialName("long") val long: Double? = null
)