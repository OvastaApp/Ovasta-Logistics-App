package com.ovasta.logisticsapp.data.setting.data.datastore

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ovasta.logisticsapp.base.constants.SharedPreferenceConstants
import com.ovasta.logisticsapp.base.encryption.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

/**
 * The session in the same encrypted DataStore file the app has always used, so existing
 * sessions survive the move to :shared. Create only one per process.
 */
fun createAndroidSessionStore(context: Context): SessionStore = DataStoreSessionStore(
    DataStoreFactory.create(
        serializer = SessionPreferencesSerializer,
        produceFile = {
            context.preferencesDataStoreFile(SharedPreferenceConstants.PREFERENCE_NAME)
        }
    )
)

private class DataStoreSessionStore(private val dataStore: DataStore<SessionPreferences>) : SessionStore {
    override val data: Flow<SessionPreferences> = dataStore.data

    override suspend fun update(transform: (SessionPreferences) -> SessionPreferences) =
        dataStore.updateData { transform(it) }
}

/** JSON encrypted with the Android Keystore key ([Crypto]) and Base64-encoded. */
private object SessionPreferencesSerializer : Serializer<SessionPreferences> {
    override val defaultValue: SessionPreferences
        get() = SessionPreferences()

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

            SessionJson.decode(jsonString)

        } catch (e: Exception) {
            e.printStackTrace()

            defaultValue
        }
    }

    override suspend fun writeTo(
        t: SessionPreferences,
        output: OutputStream
    ) {
        val bytes = SessionJson.encode(t).toByteArray()

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
