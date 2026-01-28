package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.ovasta.logisticsapp.base.BaseViewModel

@Composable
fun NavControllerEventHandler(
    viewModel: BaseViewModel,
    navController: NavController
) {
    LaunchedEffect(viewModel.navControllerEvent) {
        viewModel.navControllerEvent.collect { event ->
            event?.invoke(navController)
        }
    }
}