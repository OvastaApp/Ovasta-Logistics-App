package com.ovasta.logisticsapp.data.setting.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    @SerialName("fcm_token") val fcmToken: String
)