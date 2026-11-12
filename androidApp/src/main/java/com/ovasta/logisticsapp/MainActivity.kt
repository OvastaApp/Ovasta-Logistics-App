package com.ovasta.logisticsapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.ovasta.logisticsapp.base.local.LocaleHelper
import com.ovasta.logisticsapp.presentation.nav.PendingOrderLink
import com.ovasta.logisticsapp.shared.App

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applySavedLocale(newBase))
    }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ActivityCompat.requestPermissions(
            this, permissions, 100
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // A recreated Activity (language change) was already handled
        if (savedInstanceState == null) openTappedOrder(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        openTappedOrder(intent)
    }

    /**
     * A tapped push's data arrives as extras: from FcmService's notification, or from the one the
     * system shows while the app is in the background.
     */
    private fun openTappedOrder(intent: Intent?) {
        val extras = intent?.extras ?: return
        PendingOrderLink.onNotificationTapped(
            type = extras.getString(PendingOrderLink.KEY_TYPE),
            orderId = extras.getString(PendingOrderLink.KEY_ORDER_ID)
        )
    }
}
