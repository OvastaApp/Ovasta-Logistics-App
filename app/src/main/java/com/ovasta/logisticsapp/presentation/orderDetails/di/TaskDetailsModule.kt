package com.ovasta.logisticsapp.presentation.orderDetails.di

import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsApi
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.TaskDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val taskDetailsModule = module {
    factory { get<Retrofit>().create(OrderDetailsApi::class.java) }
    single<IOrderDetailsRemoteDataSource> { OrderDetailsRemoteDataSource(get(), get()) }
    single<IOrderDetailsRepository> { OrderDetailsRepository(get()) }
    viewModel { TaskDetailsViewModel(get(), get()) }
}