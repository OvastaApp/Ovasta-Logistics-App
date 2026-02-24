package com.ovasta.logisticsapp.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import com.ovasta.logisticsapp.base.di.startKoin
import com.ovasta.logisticsapp.base.interceptor.SessionHeaderCache
import com.ovasta.logisticsapp.base.services.LocationTrackerService
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LocationTrackerService.LOCATION_CHANNEL,
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}