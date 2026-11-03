package com.ovasta.logisticsapp.base.di

import com.ovasta.logisticsapp.presentation.home.data.OrderAlarm
import org.koin.dsl.bind
import com.ovasta.logisticsapp.base.ext.OrderAlarmSound
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val hapticsModule = module {
    single { OrderAlarmSound(androidContext()) } bind OrderAlarm::class
}

