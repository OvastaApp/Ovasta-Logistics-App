package com.ovasta.logisticsapp.listener

import androidx.compose.runtime.staticCompositionLocalOf

interface LogoutListener {
    fun onUnauthorized()
}

/** Told when a request comes back unauthorized (401); none is provided yet. */
val LocalLogoutListener = staticCompositionLocalOf<LogoutListener?> { null }
