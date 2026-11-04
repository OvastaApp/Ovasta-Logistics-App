package com.ovasta.logisticsapp

import com.ovasta.logisticsapp.base.local.AppLanguageStore
import com.ovasta.logisticsapp.base.services.trackingModule
import com.ovasta.logisticsapp.base.services.LocationTrackingSwitch
import com.ovasta.logisticsapp.data.remote.NetworkErrorMessages
import com.ovasta.logisticsapp.data.remote.PushTokenProvider
import com.ovasta.logisticsapp.data.remote.firebaseModule
import com.ovasta.logisticsapp.data.remote.networkModule
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import com.ovasta.logisticsapp.data.setting.sessionModule
import com.ovasta.logisticsapp.presentation.home.data.DeviceLocation
import com.ovasta.logisticsapp.presentation.home.data.TrackingKeepAlive
import com.ovasta.logisticsapp.presentation.home.data.OrderAlarm
import com.ovasta.logisticsapp.presentation.home.data.SeenTaskStore
import com.ovasta.logisticsapp.presentation.viewModelModule
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.Logger
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinModulesTest {

    /**
     * Every constructor parameter of the shared definitions is either defined by a shared module
     * or is one of these platform bindings, which each app must provide (Android: remoteModule,
     * settingModule, homeModule, profileModule, hapticsModule).
     */
    private val platformBindings = listOf(
        HttpClientEngine::class,
        NetworkErrorMessages::class,
        Logger::class,
        SessionStore::class,
        PushTokenProvider::class,
        TrackingKeepAlive::class,
        OrderAlarm::class,
        DeviceLocation::class,
        SeenTaskStore::class,
        LocationTrackingSwitch::class,
        AppLanguageStore::class,
    )

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `shared modules resolve with the platform bindings`() {
        module { includes(networkModule, sessionModule, firebaseModule, trackingModule, viewModelModule) }
            .verify(extraTypes = platformBindings)
    }
}
