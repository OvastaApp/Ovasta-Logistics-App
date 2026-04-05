package com.ovasta.logisticsapp.presentation.orderDetails.di

import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.TaskDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskDetailsModule = module {
    single<IOrderDetailsRemoteDataSource> { OrderDetailsRemoteDataSource(get()) }
    single<IOrderDetailsRepository> { OrderDetailsRepository(get()) }
    viewModel { TaskDetailsViewModel(get(), get()) }
}