package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class PartnerStatus(
    @SerializedName("is_online") val isOnline: Boolean,
)