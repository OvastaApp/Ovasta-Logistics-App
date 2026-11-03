package com.ovasta.logisticsapp.presentation.profile.di

import android.content.Context
import com.ovasta.logisticsapp.base.local.AppLanguageStore
import com.ovasta.logisticsapp.base.local.LocaleHelper
import com.ovasta.logisticsapp.base.services.LocationTrackingSwitch
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val profileModule = module {
    single<AppLanguageStore> { LocaleHelperLanguageStore(androidContext()) }
    single { LocationTrackingSwitch { get<IHomeRepository>().stopLocationTracking() } }
}

/** The app language as LocaleHelper keeps it, so attachBaseContext can read it before Koin. */
private class LocaleHelperLanguageStore(private val context: Context) : AppLanguageStore {
    override fun language(): String = LocaleHelper.getLanguage(context)
    override fun setLanguage(code: String) = LocaleHelper.setLanguage(context, code)
}
