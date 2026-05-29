package com.ovasta.logisticsapp.base.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
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
    }
}
