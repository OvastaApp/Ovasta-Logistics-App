package com.ovasta.logisticsapp.presentation

import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginViewModel
import com.ovasta.logisticsapp.presentation.auth.splash.SplashViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.availableTasks.AvailableTasksViewModel
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.TaskDetailsViewModel
import com.ovasta.logisticsapp.presentation.profile.presentation.ProfileViewModel
import com.ovasta.logisticsapp.presentation.profile.presentation.charge.ChargeViewModel
import com.ovasta.logisticsapp.presentation.profile.presentation.wallet.WalletViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * The screens' ViewModels. Besides the shared modules they need the platform's OrderAlarm,
 * DeviceLocation, SeenTaskStore, IHomeRepository, LocationTrackingGuard, LocationTrackingSwitch,
 * AppLanguageStore and PushTokenProvider (the Android app's homeModule, profileModule and
 * remoteModule).
 */
val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { AvailableTasksViewModel(get(), get()) }
    viewModel { TaskDetailsViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { WalletViewModel(get(), get()) }
    viewModel { ChargeViewModel(get()) }
}
