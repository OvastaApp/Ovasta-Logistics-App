package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.navigation.NavController
import com.ovasta.logisticsapp.base.ScreenDirection
import kotlinx.serialization.Serializable

@Serializable
object OrderDetailsScreen : ScreenDirection {
    override fun execute(navController: NavController) {
        navController.navigate(OrderDetailsScreen)
    }
}


