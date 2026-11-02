package com.ovasta.logisticsapp.base.local.di

import com.ovasta.logisticsapp.base.local.AppResources
import com.ovasta.logisticsapp.base.local.repository.ResourcesRepository
import org.koin.dsl.module


val resourcesModule = module {
    single { AppResources(get()) }
    single { ResourcesRepository(get()) }
}