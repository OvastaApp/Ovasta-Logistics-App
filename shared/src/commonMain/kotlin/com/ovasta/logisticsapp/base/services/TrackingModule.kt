package com.ovasta.logisticsapp.base.services

import org.koin.dsl.bind
import org.koin.dsl.module

/** The tracking health monitor that gates order actions; needs the platform's DeviceLocation. */
val trackingModule = module {
    single { LocationTrackingMonitor(get()) } bind LocationTrackingGuard::class
}
