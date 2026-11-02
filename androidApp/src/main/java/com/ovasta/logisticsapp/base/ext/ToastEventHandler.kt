package com.ovasta.logisticsapp.base.ext

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.ovasta.logisticsapp.base.BaseViewModel

@Composable
fun ToastEventHandler(
    viewModel: BaseViewModel,
    context: Context = LocalContext.current
) {
    LaunchedEffect(viewModel.toastEvent) {
        viewModel.toastEvent.collect { event ->
            when (event) {
                is ToastEvent.ResourceToastEvent -> {
                    val message = context.getString(event.resId, *event.args)
                    Toast.makeText(context, message, event.duration).show()
                }

                is ToastEvent.StringToastEvent -> Toast.makeText(context, event.message, event.duration).show()

                else -> {}
            }
        }
    }
}