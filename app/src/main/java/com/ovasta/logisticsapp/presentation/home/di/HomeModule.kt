package com.ovasta.logisticsapp.presentation.home.di

import com.ovasta.logisticsapp.base.services.LocationManager
import com.ovasta.logisticsapp.presentation.home.data.HomeApi
import com.ovasta.logisticsapp.presentation.home.data.HomeFirebaseRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.HomeRepository
import com.ovasta.logisticsapp.presentation.home.data.HomeServerRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.IHomeFirebaseRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeServerRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.availableTasks.AvailableTasksViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val homeModule = module {
    factory { get<Retrofit>().create(HomeApi::class.java) }
    single<IHomeFirebaseRemoteDataSource> { HomeFirebaseRemoteDataSource(get()) }
    single<IHomeServerRemoteDataSource> { HomeServerRemoteDataSource(get()) }
    single<IHomeRepository> { HomeRepository(get(), get(), get()) }
    single { LocationManager(androidContext()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { AvailableTasksViewModel(get(), get()) }
}