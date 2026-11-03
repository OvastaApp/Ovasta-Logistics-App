package com.ovasta.logisticsapp.base.di

import com.ovasta.logisticsapp.data.remote.networkModule
import com.ovasta.logisticsapp.data.remote.firebaseModule
import com.ovasta.logisticsapp.data.setting.sessionModule
import com.ovasta.logisticsapp.presentation.viewModelModule
import android.app.Application
import com.ovasta.logisticsapp.base.crashlyticsInfo.di.crashlyticsInfoModule
import com.ovasta.logisticsapp.base.local.di.resourcesModule
import com.ovasta.logisticsapp.data.setting.di.settingModule
import com.ovasta.logisticsapp.presentation.home.di.homeModule
import com.ovasta.logisticsapp.presentation.profile.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/** The shared modules plus the Android bindings they need (checked by KoinGraphTest). */
val appModules = listOf(
    localModule,
    remoteModule,
    networkModule,
    sessionModule,
    settingModule,
    firebaseModule,
    resourcesModule,
    hapticsModule,
    homeModule,
    profileModule,
    viewModelModule,
    crashlyticsInfoModule,
)

fun startKoin(application: Application) {
    startKoin {
        androidContext(application)
        printLogger(Level.DEBUG)
        modules(appModules)
    }
}