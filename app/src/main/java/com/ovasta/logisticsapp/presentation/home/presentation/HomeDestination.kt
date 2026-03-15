package com.ovasta.logisticsapp.presentation.home.presentation

import androidx.navigation.NavController
import com.ovasta.logisticsapp.base.ScreenDirection
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen : ScreenDirection {
    override fun execute(navController: NavController) {
        navController.navigate(HomeScreen) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}

