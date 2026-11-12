package com.ovasta.logisticsapp.shared

import com.ovasta.logisticsapp.base.interceptor.SessionHeaderCache
import com.ovasta.logisticsapp.base.local.AppLanguageStore
import com.ovasta.logisticsapp.base.local.UserDefaultsLanguageStore
import com.ovasta.logisticsapp.base.platform.IosDeviceLocation
import com.ovasta.logisticsapp.base.platform.IosTrackingKeepAlive
import com.ovasta.logisticsapp.base.services.LocationTrackingSwitch
import com.ovasta.logisticsapp.base.services.trackingModule
import com.ovasta.logisticsapp.data.remote.IosNetworkErrorMessages
import com.ovasta.logisticsapp.data.remote.NetworkErrorMessages
import com.ovasta.logisticsapp.data.remote.PushTokenBridge
import com.ovasta.logisticsapp.data.remote.PushTokenProvider
import com.ovasta.logisticsapp.data.remote.createIosHttpEngine
import com.ovasta.logisticsapp.data.remote.firebaseModule
import com.ovasta.logisticsapp.data.remote.networkModule
import com.ovasta.logisticsapp.data.setting.data.datastore.FileSessionStore
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import com.ovasta.logisticsapp.data.setting.sessionModule
import com.ovasta.logisticsapp.presentation.home.data.DeviceLocation
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.IosOrderAlarm
import com.ovasta.logisticsapp.presentation.home.data.OrderAlarm
import com.ovasta.logisticsapp.presentation.home.data.SeenTaskStore
import com.ovasta.logisticsapp.presentation.home.data.TrackingKeepAlive
import com.ovasta.logisticsapp.presentation.home.data.UserDefaultsSeenTaskStore
import com.ovasta.logisticsapp.presentation.viewModelModule
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.Logger
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.experimental.ExperimentalNativeApi

/** What the shared modules need from iOS (the Android app's remoteModule, homeModule...). */
@OptIn(ExperimentalNativeApi::class)
private val iosModule = module {
    single<HttpClientEngine> { createIosHttpEngine() }
    single { IosNetworkErrorMessages() } bind NetworkErrorMessages::class
    single<SessionStore> { FileSessionStore() }
    single { PushTokenProvider { PushTokenBridge.token } }
    single { IosDeviceLocation() } bind DeviceLocation::class
    single<TrackingKeepAlive> { IosTrackingKeepAlive(lazy { get() }, get()) }
    single<OrderAlarm> { IosOrderAlarm() }
    single<SeenTaskStore> { UserDefaultsSeenTaskStore() }
    single { LocationTrackingSwitch { get<IHomeRepository>().stopLocationTracking() } }
    single<AppLanguageStore> { UserDefaultsLanguageStore() }
    if (Platform.isDebugBinary) {
        single<Logger> {
            object : Logger {
                override fun log(message: String) {
                    println("HttpClient: $message")
                }
            }
        }
    }
}

/**
 * Starts Koin with the shared modules and the iOS bindings (checked by KoinModulesTest).
 * Called once by the AppDelegate, after FirebaseApp.configure(); Swift sees it as
 * `KoinIosKt.doInitKoin()`.
 */
fun initKoin() {
    val koin = startKoin {
        modules(iosModule, networkModule, sessionModule, firebaseModule, trackingModule, viewModelModule)
    }.koin
    // Before the first request, or it goes out without the saved token. The session file is
    // read when the store is created, so this returns at once.
    runBlocking { SessionHeaderCache.initialize(koin.get()) }
    koin.get<IosNetworkErrorMessages>().reload()
}

internal fun koin() = KoinPlatform.getKoin()
