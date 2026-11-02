package com.ovasta.logisticsapp.data.setting.di

import android.location.Geocoder
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import com.ovasta.logisticsapp.data.setting.data.datastore.createAndroidSessionStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.Locale

/** What the shared sessionModule needs from the Android app, plus Android-only settings. */
val settingModule = module {
    single { Geocoder(androidContext(), Locale.getDefault()) }
    single<SessionStore> { createAndroidSessionStore(androidContext()) }
}
