package com.ovasta.logisticsapp.data.remote

/** This device's push (FCM) token, sent with the login; null when it can't be read. */
fun interface PushTokenProvider {
    suspend fun pushToken(): String?
}
