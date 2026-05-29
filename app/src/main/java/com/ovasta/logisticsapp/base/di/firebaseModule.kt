package com.ovasta.logisticsapp.base.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.koin.dsl.module

val firebaseModule = module {

    single<FirebaseFirestore> {

        val firestore = FirebaseFirestore.getInstance()

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false) // 👈 THIS disables cache completely
            .build()

        firestore.firestoreSettings = settings

        firestore
    }
}