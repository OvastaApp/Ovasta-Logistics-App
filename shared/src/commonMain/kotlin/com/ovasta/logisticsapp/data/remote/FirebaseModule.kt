package com.ovasta.logisticsapp.data.remote

import com.ovasta.logisticsapp.presentation.auth.login.data.ILoginRemoteDataSource
import com.ovasta.logisticsapp.presentation.auth.login.data.ILoginRepository
import com.ovasta.logisticsapp.presentation.auth.login.data.LoginRemoteDataSource
import com.ovasta.logisticsapp.presentation.auth.login.data.LoginRepository
import com.ovasta.logisticsapp.presentation.home.data.HomeFirebaseRemoteDataSource
import com.ovasta.logisticsapp.presentation.home.data.IHomeFirebaseRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRemoteDataSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

/** Firestore, Auth and the data sources built on them. Firebase must be initialized first. */
val firebaseModule = module {
    single<FirebaseFirestore> {
        // No disk cache: orders must always reflect the server, as before the move to GitLive
        Firebase.firestore.apply { setSettings(persistenceEnabled = false) }
    }
    single { Firebase.auth }

    single<IHomeFirebaseRemoteDataSource> { HomeFirebaseRemoteDataSource(get()) }
    single<IOrderDetailsRemoteDataSource> { OrderDetailsRemoteDataSource(get(), get()) }
    single<IOrderDetailsRepository> { OrderDetailsRepository(get()) }
    single<ILoginRemoteDataSource> { LoginRemoteDataSource(get(), get()) }
    single<ILoginRepository> { LoginRepository(get()) }
}
