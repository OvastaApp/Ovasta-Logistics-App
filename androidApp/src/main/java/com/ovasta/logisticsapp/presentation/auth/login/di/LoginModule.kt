package com.ovasta.logisticsapp.presentation.auth.login.di

import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    viewModel { LoginViewModel(get(), get(), get()) }
}