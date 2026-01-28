package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.ovasta.logisticsapp.base.BaseViewModel

@Composable
fun ContextEventHandler(viewModel: BaseViewModel) {
    val context = LocalContext.current
    LaunchedEffect(viewModel.contextEvent) {
        viewModel.contextEvent.collect { event ->
            event?.invoke(context)
        }
    }
}