package com.ovasta.logisticsapp.base.di

import com.ovasta.logisticsapp.base.ext.OrderAlarmSound
import com.ovasta.logisticsapp.base.ext.OrderVibrator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val hapticsModule = module {
    single { OrderVibrator(androidContext()) }
    single { OrderAlarmSound(androidContext()) }
}

