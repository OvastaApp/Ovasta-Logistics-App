package com.ovasta.logisticsapp.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.datastore.core.DataStore
import com.google.firebase.messaging.FirebaseMessaging
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.di.startKoin
import com.ovasta.logisticsapp.base.interceptor.SessionHeaderCache
import com.ovasta.logisticsapp.base.services.LocationTrackerService
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import kotlin.getValue

class LogisticsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this@LogisticsApp)
        val sessionDataStore: DataStore<SessionPreferences> by inject()
        CoroutineScope(Dispatchers.IO).launch {
            SessionHeaderCache.initialize(sessionDataStore)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LocationTrackerService.LOCATION_CHANNEL,
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )

            val soundUri = Uri.parse("android.resource://${packageName}/${R.raw.new_order_alert}")
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val orderChannel = NotificationChannel(
                DELIVERY_TASK_CHANNEL,
                "Delivery Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                description = "New delivery task notifications"
            }

            val backgroundChannel = NotificationChannel(
                DELIVERY_TASK_BACKGROUND_CHANNEL,
                "Delivery Tasks (Background)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                description = "Delivery task notifications when app is in background"
            }

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Delete old channels so sound settings take effect
            notificationManager.deleteNotificationChannel("delivery_task_channel")
            notificationManager.deleteNotificationChannel("delivery_task_channel_v1")

            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(orderChannel)
            notificationManager.createNotificationChannel(backgroundChannel)
        }

        checkAndSendFcmToken()
    }

    private fun checkAndSendFcmToken() {
        val settingsRepository: ISettingsRepository by inject()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = settingsRepository.getUseData()
                if (user != null) {
                    val cachedToken = settingsRepository.getFcmToken()
                    if (cachedToken.isEmpty()) {
                        val token = FirebaseMessaging.getInstance().token.await()
                        settingsRepository.updateFcmToken(token)

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    companion object {
        const val DELIVERY_TASK_CHANNEL = "delivery_task_channel_v2"
        const val DELIVERY_TASK_BACKGROUND_CHANNEL = "delivery_task_background_channel"
    }
}