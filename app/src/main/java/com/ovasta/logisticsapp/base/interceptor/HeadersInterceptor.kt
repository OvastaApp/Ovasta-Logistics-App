package com.ovasta.logisticsapp.base.interceptor

import androidx.datastore.core.DataStore
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.data.RemoteConstants.HeadersConst.ACCEPT
import com.ovasta.logisticsapp.data.RemoteConstants.HeadersConst.AUTHORIZATION
import com.ovasta.logisticsapp.data.RemoteConstants.HeadersConst.IDENTIFIER
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

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

class HeadersInterceptor(private val dataStore: DataStore<SessionPreferences>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(setupRequestHeaders(chain.request()))
    }

    private fun setupRequestHeaders(request: Request): Request {
        val hash = "\$2y\$10\$Lku1/D.Uk5XmKPxiMFYxmOi6KqxmqJusRa3aNosKJCDVD9dDQz/b2"


        val builder = request.newBuilder()
//        builder.addHeader(VERSION, BuildConfig.VERSION_CODE.toString())
//        builder.addHeader(DEVICE_MODEL, Build.MODEL)
//        builder.addHeader(ANDROID_VERSION, Build.VERSION.SDK_INT.toString())
        builder.addHeader(ACCEPT, "application/json")
        builder.addHeader(IDENTIFIER, hash)

//        builder.addHeader(LANG, SessionHeaderCache.lang)
//        builder.addHeader(DEVICE_ID, SessionHeaderCache.deviceId)
        val token = SessionHeaderCache.token
        builder.addHeader(
            AUTHORIZATION,
            token
        )
        return builder.build()
    }
}