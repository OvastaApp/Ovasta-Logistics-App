package com.ovasta.logisticsapp.base.interceptor

import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object SessionHeaderCache {
    var lang: String = LANGUAGE_AR_ISO
    var deviceId: String = ""
    var token: String = ""

    suspend fun initialize(sessionStore: SessionStore) {
        val session = sessionStore.data.first()
        lang = session.userLang
        deviceId = session.deviceId
        token = session.accessToken

        CoroutineScope(Dispatchers.IO).launch {
            launch {
                sessionStore.data.collect {
                    lang = it.userLang
                }
            }
            launch {
                sessionStore.data.collect {
                    deviceId = it.deviceId
                }
            }
            launch {
                sessionStore.data.collect {
                    token = it.accessToken
                }
            }

        }
    }
}
