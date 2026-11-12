package com.ovasta.logisticsapp.base.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ovasta.logisticsapp.base.BaseViewModel
import org.jetbrains.compose.resources.getString

@Composable
fun ToastEventHandler(viewModel: BaseViewModel) {
    val showToast = rememberPlatformToaster()
    LaunchedEffect(viewModel.toastEvent) {
        viewModel.toastEvent.collect { event ->
            when (event) {
                is ToastEvent.ResourceToastEvent -> showToast(getString(event.resource, *event.args))
                is ToastEvent.StringToastEvent -> showToast(event.message)
                null -> {}
            }
        }
    }
}

/** Shows a short message the way the platform does (a Toast on Android). */
@Composable
internal expect fun rememberPlatformToaster(): (String) -> Unit
