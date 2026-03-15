package com.ovasta.logisticsapp.presentation.auth.login.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("password") val password: String,
    @SerializedName("type_id") val userTypeId: Int
)