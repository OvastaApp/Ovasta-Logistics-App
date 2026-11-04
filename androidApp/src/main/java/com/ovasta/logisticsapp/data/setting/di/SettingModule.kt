package com.ovasta.logisticsapp.data.setting.di

import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import com.ovasta.logisticsapp.data.setting.data.datastore.createAndroidSessionStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** What the shared sessionModule needs from the Android app. */
val settingModule = module {
    single<SessionStore> { createAndroidSessionStore(androidContext()) }
}
