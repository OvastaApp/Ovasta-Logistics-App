package com.ovasta.logisticsapp.presentation.profile.di

import com.ovasta.logisticsapp.presentation.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel { ProfileViewModel(get()) }
}
