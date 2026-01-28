package com.ovasta.logisticsapp.presentation.auth.splash

import androidx.navigation.NavController
import com.ovasta.logisticsapp.base.ScreenDirection
import kotlinx.serialization.Serializable

@Serializable
object SplashScreen : ScreenDirection {
    override fun execute(navController: NavController) {
        navController.navigate(SplashScreen)
    }
}
