package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps

@Composable
fun ConfirmStatusDialog(
    status: OrderSteps,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title = when (status) {
        is OrderSteps.Picked -> stringResource(R.string.confirm_pickup)
        is OrderSteps.Delivered -> stringResource(R.string.confirm_delivery)
        else -> ""
    }
    val message = when (status) {
        is OrderSteps.Picked -> stringResource(R.string.confirm_pickup_message)
        is OrderSteps.Delivered -> stringResource(R.string.confirm_delivery_message)
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}
