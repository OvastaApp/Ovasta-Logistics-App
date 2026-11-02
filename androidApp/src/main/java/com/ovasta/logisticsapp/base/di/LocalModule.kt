package com.ovasta.logisticsapp.base.di


import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ovasta.logisticsapp.base.constants.SharedPreferenceConstants

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val localModule = module {
    single {
        EncryptedSharedPreferences.create(
            androidApplication(),
            SharedPreferenceConstants.PREFERENCE_NAME,
            MasterKey.Builder(androidApplication())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}