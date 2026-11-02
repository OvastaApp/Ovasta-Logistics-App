package com.ovasta.logisticsapp.base.interceptor

import androidx.datastore.core.DataStore
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object SessionHeaderCache {
    var lang: String = LANGUAGE_AR_ISO
    var deviceId: String = ""
    var token: String = ""

    suspend fun initialize(dataStore: DataStore<SessionPreferences>) {
        val session = dataStore.data.first()
        lang = session.userLang
        deviceId = session.deviceId
        token = session.accessToken

        CoroutineScope(Dispatchers.IO).launch {
            launch {
                dataStore.data.collect {
                    lang = it.userLang
                }
            }
            launch {
                dataStore.data.collect {
                    deviceId = it.deviceId
                }
            }
            launch {
                dataStore.data.collect {
                    token = it.accessToken
                }
            }

        }
    }
}

