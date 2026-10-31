package com.ovasta.logisticsapp.base.di

import com.google.gson.GsonBuilder
import com.ovasta.logisticsapp.base.interceptor.CacheProviderInterceptor
import com.ovasta.logisticsapp.base.interceptor.ErrorMappingInterceptor
import com.ovasta.logisticsapp.base.interceptor.HeadersInterceptor
import com.ovasta.logisticsapp.data.RemoteConstants
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ovasta.logisticsapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val remoteModule = module {
    single { HeadersInterceptor(get()) }
    single<FirebaseFirestore> { Firebase.firestore }
    single { HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY) }
    single { ErrorMappingInterceptor(get(), get()) }
    single { CacheProviderInterceptor.provideCache(get()) }

    single {
        val builder = OkHttpClient.Builder()
        val protocols = mutableListOf<Protocol>()
        protocols.add(Protocol.HTTP_1_1)
        builder
            .cache(get())
            .addInterceptor(get<HeadersInterceptor>())
            .addInterceptor(get<ErrorMappingInterceptor>())
            .connectTimeout(RemoteConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(RemoteConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(RemoteConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .protocols(protocols)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(get<HttpLoggingInterceptor>())
        }
        builder.build()
    }

    single { GsonBuilder().create() }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://b79d-2001-8f8-1425-28a3-6f11-8a4e-572b-a879.ngrok-free.app/api/logistics/")
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
            .build()
    }

}