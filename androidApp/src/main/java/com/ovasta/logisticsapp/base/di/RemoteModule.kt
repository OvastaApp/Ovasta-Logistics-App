package com.ovasta.logisticsapp.base.di

import android.util.Log
import com.ovasta.logisticsapp.BuildConfig
import com.ovasta.logisticsapp.base.interceptor.SessionHeaderCache
import com.ovasta.logisticsapp.base.local.repository.ResourcesRepository
import com.ovasta.logisticsapp.data.remote.AccessTokenProvider
import com.ovasta.logisticsapp.data.remote.NetworkErrorMessages
import com.ovasta.logisticsapp.data.remote.createAndroidHttpEngine
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** What the shared networkModule needs from the Android app. */
val remoteModule = module {
    single<HttpClientEngine> { createAndroidHttpEngine(androidContext().cacheDir) }
    single<NetworkErrorMessages> { get<ResourcesRepository>() }
    single { AccessTokenProvider { SessionHeaderCache.token } }
    if (BuildConfig.DEBUG) {
        single<Logger> {
            object : Logger {
                override fun log(message: String) {
                    Log.d("HttpClient", message)
                }
            }
        }
    }
}
