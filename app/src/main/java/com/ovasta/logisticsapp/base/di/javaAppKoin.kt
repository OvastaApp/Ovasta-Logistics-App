package com.ovasta.logisticsapp.base.di

import android.app.Application
import com.ovasta.logisticsapp.base.crashlyticsInfo.di.crashlyticsInfoModule
import com.ovasta.logisticsapp.base.local.di.resourcesModule
import com.ovasta.logisticsapp.data.setting.di.settingModule
import com.ovasta.logisticsapp.presentation.auth.login.di.loginModule
import com.ovasta.logisticsapp.presentation.auth.splash.di.splashModule
import com.ovasta.logisticsapp.presentation.home.di.homeModule
import com.ovasta.logisticsapp.presentation.orderDetails.di.taskDetailsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

fun startKoin(application: Application) {
    startKoin {
        androidContext(application)
        printLogger(Level.DEBUG)
        modules(
            listOf(
                localModule,
                remoteModule,
                settingModule,
                firebaseModule,
                resourcesModule,
                hapticsModule,
                splashModule,
                loginModule,
                homeModule,
                taskDetailsModule,
                crashlyticsInfoModule,
//                invoiceModule,
//                historyModule
            )
        )
    }
}