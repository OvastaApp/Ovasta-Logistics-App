package com.ovasta.logisticsapp.data.setting.data.datastore

import androidx.datastore.core.Serializer
import com.maxab.basemodule.core.setting.model.Country
import com.maxab.basemodule.core.setting.model.RemoteConfigModel
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.base.encryption.Crypto
import com.ovasta.logisticsapp.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import android.util.Base64

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
)

object SessionPreferencesSerializer : Serializer<SessionPreferences> {
    override val defaultValue: SessionPreferences
        get() = SessionPreferences()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun readFrom(input: InputStream): SessionPreferences {
        return try {
            val encryptedBytes = withContext(Dispatchers.IO) {
                input.use { it.readBytes() }
            }
            if (encryptedBytes.isEmpty()) return defaultValue

            val jsonString = try {
                val decoded = Base64.decode(encryptedBytes, Base64.NO_WRAP)
                val decrypted = Crypto.decrypt(decoded)
                decrypted.decodeToString()
            } catch (e: Exception) {
                encryptedBytes.decodeToString()
            }

            json.decodeFromString(jsonString)

        } catch (e: Exception) {
            e.printStackTrace()

            defaultValue
        }
    }

    override suspend fun writeTo(
        t: SessionPreferences,
        output: OutputStream
    ) {
        val json = Json.encodeToString(t)
        val bytes = json.toByteArray()

        val encryptedBytes = Crypto.encrypt(bytes)

        val encryptedBytesBase64 =
            Base64.encode(encryptedBytes, Base64.DEFAULT)

        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}