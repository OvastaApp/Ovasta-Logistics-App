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

val remoteModule = module{
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

    single<Retrofit>{
            Retrofit.Builder()
                .baseUrl("http://ec2-52-47-91-27.eu-west-3.compute.amazonaws.com/api/customer-app/")
                .addConverterFactory(GsonConverterFactory.create(get()))
                .client(get())
                .build()
    }

}