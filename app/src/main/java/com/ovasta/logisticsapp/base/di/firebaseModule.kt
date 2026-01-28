package com.ovasta.logisticsapp.base.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import org.koin.dsl.module

val firebaseModule = module {

    single<FirebaseFirestore> {
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(memoryCacheSettings {})
            .build()

        FirebaseFirestore.getInstance().apply {
            firestoreSettings = settings
        }
    }
}
