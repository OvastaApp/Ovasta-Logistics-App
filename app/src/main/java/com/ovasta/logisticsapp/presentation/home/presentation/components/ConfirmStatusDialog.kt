package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseDialog
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

    BaseDialog(
        icon = painterResource(R.drawable.ic_delivery_truck),
        title = title,
        message = message,
        primaryButtonText = stringResource(R.string.confirm),
        secondaryButtonText = stringResource(R.string.dismiss),
        onPrimaryClick = onConfirm,
        onSecondaryClick = onDismiss,
        onDismiss = onDismiss
    )
}
