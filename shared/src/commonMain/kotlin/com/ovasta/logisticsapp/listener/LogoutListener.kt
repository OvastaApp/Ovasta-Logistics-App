package com.ovasta.logisticsapp.listener

import androidx.compose.runtime.staticCompositionLocalOf

interface LogoutListener {
    fun onUnauthorized()
}

/** Told when a request comes back unauthorized (401); AppNavHost provides it and logs out. */
val LocalLogoutListener = staticCompositionLocalOf<LogoutListener?> { null }
