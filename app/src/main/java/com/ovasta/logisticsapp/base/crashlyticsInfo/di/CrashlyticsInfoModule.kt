package com.ovasta.logisticsapp.base.crashlyticsInfo.di

import com.ovasta.logisticsapp.base.crashlyticsInfo.CrashlyticsInfoRemoteDataSource
import com.ovasta.logisticsapp.base.crashlyticsInfo.CrashlyticsUserInfoUseCase
import com.ovasta.logisticsapp.base.crashlyticsInfo.ICrashlyticsInfoRemoteDataSource
import org.koin.dsl.module

val crashlyticsInfoModule = module {
    single<ICrashlyticsInfoRemoteDataSource> { CrashlyticsInfoRemoteDataSource() }
    single { CrashlyticsUserInfoUseCase(get()) }
}