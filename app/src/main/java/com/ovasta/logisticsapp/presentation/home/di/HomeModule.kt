package com.ovasta.logisticsapp.presentation.home.di

import com.ovasta.logisticsapp.base.services.LocationManager
import com.ovasta.logisticsapp.presentation.home.data.HomeRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.HomeRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
//    factory { get<Retrofit>().create(LoginApi::class.java) }
    single<IHomeRemoteDataSource> { HomeRemoteDataSource(get()) }
    single<IHomeRepository> { HomeRepository(get()) }
    single { LocationManager(androidContext()) }
    viewModel { HomeViewModel(get(),get() ,get()) }
}