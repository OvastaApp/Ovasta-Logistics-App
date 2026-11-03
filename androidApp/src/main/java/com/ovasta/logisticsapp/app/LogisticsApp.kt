package com.ovasta.logisticsapp.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.crashlyticsInfo.CrashlyticsUserInfoUseCase
import com.ovasta.logisticsapp.base.di.startKoin
import com.ovasta.logisticsapp.base.interceptor.SessionHeaderCache
import com.ovasta.logisticsapp.base.local.LocaleHelper
import com.ovasta.logisticsapp.base.services.LocationTrackerService
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import kotlin.getValue

class LogisticsApp : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.applySavedLocale(base))
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this@LogisticsApp)
        val sessionStore: SessionStore by inject()
        CoroutineScope(Dispatchers.IO).launch {
            SessionHeaderCache.initialize(sessionStore)
        }

        // Set user info for Crashlytics once per session
        setUserInfoForCrashlytics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LocationTrackerService.LOCATION_CHANNEL,
                getString(R.string.channel_location),
                NotificationManager.IMPORTANCE_LOW
            )

            val soundUri = Uri.parse("android.resource://${packageName}/${R.raw.new_order_alert}")
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val orderChannel = NotificationChannel(
                DELIVERY_TASK_CHANNEL,
                getString(R.string.channel_delivery_tasks),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                description = getString(R.string.channel_delivery_tasks_description)
            }

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Delete old channels so sound settings take effect. The background channel was
            // never posted to: every new-order notification uses the channel with the sound.
            notificationManager.deleteNotificationChannel("delivery_task_channel")
            notificationManager.deleteNotificationChannel("delivery_task_channel_v1")
            notificationManager.deleteNotificationChannel("delivery_task_background_channel")

            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(orderChannel)
        }

        checkAndSendFcmToken()
    }

    private fun setUserInfoForCrashlytics() {
        val crashlyticsUserInfoUseCase: CrashlyticsUserInfoUseCase by inject()
        val settingsRepository: ISettingsRepository by inject()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = settingsRepository.getUseData()
                crashlyticsUserInfoUseCase(user)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
    }
}