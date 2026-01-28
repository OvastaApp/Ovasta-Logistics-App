package com.ovasta.logisticsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.ovasta.logisticsapp.presentation.nav.AppNavHost
import com.ovasta.logisticsapp.ui.theme.OvastaLogisticsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            OvastaLogisticsAppTheme {
                AppNavHost()
            }
        }
    }
}
