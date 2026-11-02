package com.ovasta.logisticsapp.presentation.home.di

import com.ovasta.logisticsapp.base.services.LocationManager
import com.ovasta.logisticsapp.base.services.LocationTrackingMonitor
import com.ovasta.logisticsapp.presentation.home.data.HomeFirebaseRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.HomeRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeFirebaseRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.availableTasks.AvailableTasksViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    single<IHomeFirebaseRemoteDataSource> { HomeFirebaseRemoteDataSource(get()) }
    single<IHomeRepository> { HomeRepository(get(), get(), get()) }
    single { LocationManager(androidContext()) }
    single { LocationTrackingMonitor(androidContext()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { AvailableTasksViewModel(get(), get()) }
}