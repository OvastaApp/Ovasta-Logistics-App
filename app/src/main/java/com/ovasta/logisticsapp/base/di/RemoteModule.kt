package com.ovasta.logisticsapp.base.di

import com.ovasta.logisticsapp.base.interceptor.CacheProviderInterceptor
import com.ovasta.logisticsapp.base.interceptor.ErrorMappingInterceptor
import com.ovasta.logisticsapp.base.interceptor.HeadersInterceptor
import com.ovasta.logisticsapp.data.RemoteConstants
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.ovasta.logisticsapp.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

// Lenient like Gson was: unknown keys, nulls and missing fields fall back to defaults,
// null fields are left out of request bodies
val apiJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
    encodeDefaults = true
    isLenient = true
}

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

    single { apiJson }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("http://167.172.209.252/api/logistics/")
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .client(get())
            .build()
    }

}