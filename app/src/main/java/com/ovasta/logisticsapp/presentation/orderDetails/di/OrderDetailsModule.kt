package com.ovasta.logisticsapp.presentation.orderDetails.di

import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.OrderDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    single<IOrderDetailsRemoteDataSource> { OrderDetailsRemoteDataSource() }
    single<IOrderDetailsRepository> { OrderDetailsRepository(get()) }
    viewModel { OrderDetailsViewModel(get(), get()) }
}