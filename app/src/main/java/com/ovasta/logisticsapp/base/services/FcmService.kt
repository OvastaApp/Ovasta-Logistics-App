package com.ovasta.logisticsapp.base.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.app.LogisticsApp
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FcmService : FirebaseMessagingService() {

    private val settingsRepository: ISettingsRepository by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = settingsRepository.getUseData()
                if (user != null) {
                    settingsRepository.updateFcmToken(token)
                } else {
                    settingsRepository.saveFcmToken(token)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val isAppInForeground = ProcessLifecycleOwner.get().lifecycle.currentState
            .isAtLeast(Lifecycle.State.RESUMED)
        if (!isAppInForeground) {
            showNotification(message)
        }
    }

    private fun showNotification(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: getString(R.string.new_delivery_tasks)
        val body = message.notification?.body ?: message.data["body"] ?: ""

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isAppInBackground = ProcessLifecycleOwner.get().lifecycle.currentState
            .isAtLeast(Lifecycle.State.CREATED)

        val channelId = if (isAppInBackground) {
            LogisticsApp.DELIVERY_TASK_BACKGROUND_CHANNEL
        } else {
            LogisticsApp.DELIVERY_TASK_CHANNEL
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (isAppInBackground) {
            // App in background - use system default sound
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND)
        } else {
            // App destroyed - use custom sound
            val soundUri = Uri.parse("android.resource://${packageName}/${R.raw.new_order_alert}")
            builder.setSound(soundUri)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
