package com.ovasta.logisticsapp.presentation.profile.di

import com.ovasta.logisticsapp.presentation.profile.data.IWalletRemoteDataSource
import com.ovasta.logisticsapp.presentation.profile.data.IWalletRepository
import com.ovasta.logisticsapp.presentation.profile.data.WalletApi
import com.ovasta.logisticsapp.presentation.profile.data.WalletRemoteDataSource
import com.ovasta.logisticsapp.presentation.profile.data.WalletRepository
import com.ovasta.logisticsapp.presentation.profile.presentation.ProfileViewModel
import com.ovasta.logisticsapp.presentation.profile.presentation.charge.ChargeViewModel
import com.ovasta.logisticsapp.presentation.profile.presentation.wallet.WalletViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val profileModule = module {
    factory { get<Retrofit>().create(WalletApi::class.java) }
    single<IWalletRemoteDataSource> { WalletRemoteDataSource(get(), get()) }
    single<IWalletRepository> { WalletRepository(get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { WalletViewModel(get(), get()) }
    viewModel { ChargeViewModel(get()) }
}
