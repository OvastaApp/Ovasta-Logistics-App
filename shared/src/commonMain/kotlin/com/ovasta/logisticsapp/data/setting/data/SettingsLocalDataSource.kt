package com.ovasta.logisticsapp.data.setting.data

import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import kotlinx.coroutines.flow.first

class SettingsLocalDataSource(private val sessionStore: SessionStore) :
    ISettingsLocalDataSource {

    override suspend fun getUseData(): User? {
        return sessionStore.data.first().user
    }

    override suspend fun clearUserData() {
        val currentLang = LANGUAGE_AR_ISO
        sessionStore.update {
            SessionPreferences(
                userLang = currentLang
            )
        }
    }

    override suspend fun saveUserData(user: User) {
        sessionStore.update {
            it.copy(
                user = user,
                accessToken = user.token ?: "",
            )
        }
    }

    override suspend fun saveFcmToken(token: String) {
        sessionStore.update {
            it.copy(fcmToken = token)
        }
    }

    override suspend fun getFcmToken(): String {
        return sessionStore.data.first().fcmToken
    }

    override suspend fun isLocationConsentShown(): Boolean {
        return sessionStore.data.first().locationConsentShown
    }

    override suspend fun setLocationConsentShown(shown: Boolean) {
        sessionStore.update { it.copy(locationConsentShown = shown) }
    }
}
