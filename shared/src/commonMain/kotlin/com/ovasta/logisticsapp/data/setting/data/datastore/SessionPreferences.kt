package com.ovasta.logisticsapp.data.setting.data.datastore

import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.data.setting.model.Country
import com.ovasta.logisticsapp.data.setting.model.RemoteConfigModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SessionPreferences(
    val userLang: String = LANGUAGE_AR_ISO,
    val user: User? = null,
    val userCountry: Country? = null,
    val remoteConfigModel: RemoteConfigModel? = null,
    val isLoggedIn: Boolean = false,
    val deviceRam: String = "",
    val accessToken: String = "",
    val deviceId: String = "",
    val isTracking: Boolean = false,
    val fcmToken: String = "",
    val locationConsentShown: Boolean = false,
)

/** How [SessionPreferences] is stored as JSON on every platform. */
internal object SessionJson {
    private val reader = Json { ignoreUnknownKeys = true }

    fun encode(session: SessionPreferences): String = Json.encodeToString(session)

    fun decode(json: String): SessionPreferences = reader.decodeFromString(json)
}
