package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class ChangeStatusRequest(
    @SerializedName("online") val isOnline: Boolean
)