package com.ovasta.logisticsapp.data.setting

import com.ovasta.logisticsapp.base.interceptor.SessionHeaderCache
import com.ovasta.logisticsapp.data.remote.AccessTokenProvider
import com.ovasta.logisticsapp.data.setting.data.ISettingsLocalDataSource
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.data.setting.data.SettingsLocalDataSource
import com.ovasta.logisticsapp.data.setting.data.SettingsRepository
import org.koin.dsl.module

/**
 * The session and settings, built on a SessionStore the app provides
 * (createAndroidSessionStore on Android, FileSessionStore on iOS).
 */
val sessionModule = module {
    single<ISettingsLocalDataSource> { SettingsLocalDataSource(get()) }
    single<ISettingsRepository> { SettingsRepository(get(), get()) }
    // Read on every request; SessionHeaderCache.initialize keeps it in step with the session
    single { AccessTokenProvider { SessionHeaderCache.token } }
}
