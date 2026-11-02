package com.ovasta.logisticsapp.data.remote

import com.ovasta.logisticsapp.data.setting.data.ISettingsRemoteDataSource
import com.ovasta.logisticsapp.presentation.auth.login.data.LoginApi
import com.ovasta.logisticsapp.presentation.home.data.HomeApi
import com.ovasta.logisticsapp.presentation.home.data.IHomeServerRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsApi
import com.ovasta.logisticsapp.presentation.profile.data.WalletApi
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

class NetworkModuleTest {

    // What the app provides (remoteModule on Android)
    private val appModule = module {
        single<HttpClientEngine> { MockEngine { respondOk() } }
        single<NetworkErrorMessages> {
            object : NetworkErrorMessages {
                override fun timeout() = "timeout"
                override fun genericUnknown() = "unknown"
            }
        }
        single { AccessTokenProvider { "" } }
    }

    @Test
    fun resolvesEveryApiAndDataSource() {
        val koin = koinApplication { modules(appModule, networkModule) }.koin

        assertNotNull(koin.get<LoginApi>())
        assertNotNull(koin.get<HomeApi>())
        assertNotNull(koin.get<OrderDetailsApi>())
        assertNotNull(koin.get<WalletApi>())
        assertNotNull(koin.get<IHomeServerRemoteDataSource>())
        assertNotNull(koin.get<ISettingsRemoteDataSource>())
    }
}
