package com.ovasta.logisticsapp.data.remote

import com.ovasta.logisticsapp.data.setting.data.ISettingsRemoteDataSource
import com.ovasta.logisticsapp.data.setting.data.SettingsApi
import com.ovasta.logisticsapp.data.setting.data.SettingsRemoteDataSource
import com.ovasta.logisticsapp.presentation.auth.login.data.LoginApi
import com.ovasta.logisticsapp.presentation.home.data.HomeApi
import com.ovasta.logisticsapp.presentation.home.data.HomeServerRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.IHomeServerRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsApi
import com.ovasta.logisticsapp.presentation.profile.data.WalletApi
import org.koin.dsl.module

/**
 * The backend client, its APIs and the remote data sources built only on them.
 * The app must also provide an HttpClientEngine, NetworkErrorMessages, an AccessTokenProvider
 * and, for debug builds, a Ktor Logger.
 */
val networkModule = module {
    single { createHttpClient(get(), get(), get(), getOrNull()) }

    single { LoginApi(get()) }
    single { HomeApi(get()) }
    single { OrderDetailsApi(get()) }
    single { WalletApi(get()) }
    single { SettingsApi(get()) }

    single<IHomeServerRemoteDataSource> { HomeServerRemoteDataSource(get()) }
    single<ISettingsRemoteDataSource> { SettingsRemoteDataSource(get()) }
}
