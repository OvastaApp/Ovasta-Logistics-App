package com.ovasta.logisticsapp.presentation.auth.splash.di

import com.ovasta.logisticsapp.presentation.auth.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}