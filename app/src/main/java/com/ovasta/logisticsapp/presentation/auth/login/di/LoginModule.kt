package com.ovasta.logisticsapp.presentation.auth.login.di

import com.ovasta.logisticsapp.presentation.auth.login.data.ILoginRemoteDataSource
import com.ovasta.logisticsapp.presentation.auth.login.data.ILoginRepository
import com.ovasta.logisticsapp.presentation.auth.login.data.LoginApi
import com.ovasta.logisticsapp.presentation.auth.login.data.LoginRemoteDataSource
import com.ovasta.logisticsapp.presentation.auth.login.data.LoginRepository
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val loginModule = module {
    factory { get<Retrofit>().create(LoginApi::class.java) }
    single<ILoginRemoteDataSource> { LoginRemoteDataSource(get()) }
    single<ILoginRepository> { LoginRepository(get()) }
    viewModel { LoginViewModel(get(), get()) }
}