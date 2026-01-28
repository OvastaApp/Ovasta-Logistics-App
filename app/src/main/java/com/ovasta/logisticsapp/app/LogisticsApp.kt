package com.ovasta.logisticsapp.app

import android.app.Application
import androidx.datastore.core.DataStore
import com.ovasta.logisticsapp.base.di.startKoin
import com.ovasta.logisticsapp.base.interceptor.SessionHeaderCache
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.getValue

class LogisticsApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin(this@LogisticsApp)
        val sessionDataStore: DataStore<SessionPreferences> by inject()
        CoroutineScope(Dispatchers.IO).launch {
            SessionHeaderCache.initialize(sessionDataStore)
        }
    }
}