package com.ovasta.logisticsapp.presentation.auth.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("mobile") val mobile: String,
    @SerialName("password") val password: String,
    @SerialName("type_id") val userTypeId: Int,
    @SerialName("fcm_token") val fcmToken: String? = null
)