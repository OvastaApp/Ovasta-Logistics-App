package com.ovasta.logisticsapp.presentation.orderDetails.di

import com.ovasta.logisticsapp.presentation.orderDetails.presentation.TaskDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskDetailsModule = module {
    viewModel { TaskDetailsViewModel(get(), get()) }
}
