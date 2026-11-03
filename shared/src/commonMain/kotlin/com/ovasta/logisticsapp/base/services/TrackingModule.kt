package com.ovasta.logisticsapp.base.services

import com.ovasta.logisticsapp.presentation.home.data.HomeRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Location tracking and the Home repository built on it. Needs the platform's DeviceLocation
 * and TrackingKeepAlive.
 */
val trackingModule = module {
    single { LocationTrackingMonitor(get()) } bind LocationTrackingGuard::class
    single { LocationTracker(get(), get()) }
    single<IHomeRepository> { HomeRepository(get(), get(), get(), get()) }
}
