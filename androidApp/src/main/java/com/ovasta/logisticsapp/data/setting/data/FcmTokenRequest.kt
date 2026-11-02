package com.ovasta.logisticsapp.data.setting.data

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class FcmTokenRequest(
    @SerialName("fcm_token") val fcmToken: String
)