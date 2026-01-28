package com.ovasta.logisticsapp.data.setting.data

import androidx.datastore.core.DataStore
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import com.ovasta.logisticsapp.data.User
import kotlinx.coroutines.flow.first

class SettingsLocalDataSource(private val dataStore: DataStore<SessionPreferences>) :
    ISettingsLocalDataSource {

    override suspend fun getUseData(): User? {
        return dataStore.data.first().user
    }
    override suspend fun clearUserData() {
        val currentLang = LANGUAGE_AR_ISO
        dataStore.updateData {
            SessionPreferences(
                userLang = currentLang
            )
        }
    }

    override suspend fun saveUserData(user: User) {
      dataStore.updateData {
          it.copy(
                user = user
          )
      }
    }
}