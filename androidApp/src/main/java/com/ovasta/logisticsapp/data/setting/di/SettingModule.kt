package com.ovasta.logisticsapp.data.setting.di

import android.content.Context
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ovasta.logisticsapp.data.setting.data.ISettingsLocalDataSource
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.data.setting.data.SettingsLocalDataSource
import com.ovasta.logisticsapp.data.setting.data.SettingsRepository
import com.ovasta.logisticsapp.base.constants.SharedPreferenceConstants
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferencesSerializer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.Locale

val settingModule = module {
    single { Geocoder(androidContext(), Locale.getDefault()) }
    single<ISettingsLocalDataSource> { SettingsLocalDataSource(get<DataStore<SessionPreferences>>()) }
    single<DataStore<SessionPreferences>> {
        DataStoreFactory.create(
            serializer = SessionPreferencesSerializer,
            produceFile = {
                get<Context>().preferencesDataStoreFile(SharedPreferenceConstants.PREFERENCE_NAME)
            }
        )
    }
    single<ISettingsRepository> { SettingsRepository(get(), get()) }
}