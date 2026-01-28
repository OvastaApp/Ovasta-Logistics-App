package com.ovasta.logisticsapp.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

interface ScreenDirection {
    fun execute(navController: NavController)
}



@Composable
fun ScreenDirectionEventHandler(
    viewModel: BaseViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.screenDirectionEvent.collect { direction ->
            direction?.execute(navController)
        }
    }
}
