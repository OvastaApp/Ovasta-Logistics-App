package com.ovasta.logisticsapp.presentation.auth.login.presentation

import androidx.navigation.NavController
import com.ovasta.logisticsapp.base.ScreenDirection
import kotlinx.serialization.Serializable

@Serializable
object LoginScreen : ScreenDirection {
    override fun execute(navController: NavController) {
        navController.navigate(LoginScreen)
    }
}


