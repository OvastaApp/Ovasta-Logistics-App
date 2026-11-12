package com.ovasta.logisticsapp.presentation.home.presentation.components

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.confirm
import com.ovasta.logisticsapp.shared.resources.confirm_delivery
import com.ovasta.logisticsapp.shared.resources.confirm_delivery_message
import com.ovasta.logisticsapp.shared.resources.confirm_pickup
import com.ovasta.logisticsapp.shared.resources.confirm_pickup_message
import com.ovasta.logisticsapp.shared.resources.dismiss
import com.ovasta.logisticsapp.shared.resources.ic_delivery_truck

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseDialog
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps

@Composable
fun ConfirmStatusDialog(
    status: OrderSteps,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title = when (status) {
        is OrderSteps.Picked -> stringResource(Res.string.confirm_pickup)
        is OrderSteps.Delivered -> stringResource(Res.string.confirm_delivery)
        else -> ""
    }
    val message = when (status) {
        is OrderSteps.Picked -> stringResource(Res.string.confirm_pickup_message)
        is OrderSteps.Delivered -> stringResource(Res.string.confirm_delivery_message)
        else -> ""
    }

    BaseDialog(
        icon = painterResource(Res.drawable.ic_delivery_truck),
        title = title,
        message = message,
        primaryButtonText = stringResource(Res.string.confirm),
        secondaryButtonText = stringResource(Res.string.dismiss),
        onPrimaryClick = onConfirm,
        onSecondaryClick = onDismiss,
        onDismiss = onDismiss
    )
}
