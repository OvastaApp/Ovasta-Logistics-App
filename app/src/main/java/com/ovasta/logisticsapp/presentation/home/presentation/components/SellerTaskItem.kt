package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.*
import com.ovasta.logisticsapp.presentation.home.data.model.AssignedDeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps

@Composable
fun SellerTaskItem(
    task: AssignedDeliveryTask,
    onCallSender: (String) -> Unit,
    onCallReceiver: (String) -> Unit,
    onStatusChangeClick: ((Int, OrderSteps) -> Unit)? = null
) {
    val statusColor = when (OrderSteps.fromStatusId(task.statusId ?: 0)) {
        OrderSteps.Pending -> StatusPending
        OrderSteps.Assigned -> StatusAssigned
        OrderSteps.Picked -> StatusPicked
        OrderSteps.Delivered -> StatusDelivered
        OrderSteps.Canceled -> StatusCanceled
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Base_white),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order ID + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "#${task.orderId}", style = mdSemiBold)
                Text(
                    text = task.statusName,
                    style = xsMedium.copy(color = statusColor),
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Origin and Destination with points and connecting line
            val isPicked = OrderSteps.fromStatusId(task.statusId ?: 0) == OrderSteps.Picked
            val originDotColor = if (isPicked) Gray500 else Primary
            val destinationDotColor = if (isPicked) Primary else Gray500
            Column(modifier = Modifier.fillMaxWidth()) {
                // Origin (Sender)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(originDotColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = task.fromAddress.ifBlank { stringResource(R.string.sender_info) },
                        style = mdMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (task.senderMobile.isNotBlank()) {
                        IconButton(
                            onClick = { onCallSender(task.senderMobile) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Call Sender",
                                tint = Base_white,
                                modifier = Modifier
                                    .background(SellerAction, RoundedCornerShape(20.dp))
                                    .padding(8.dp)
                            )
                        }
                    }
                }
                // Connecting vertical line
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .width(2.dp)
                        .height(32.dp)
                        .background(Gray500.copy(alpha = 0.4f))
                )
                // Destination (Receiver)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(destinationDotColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = task.toAddress.ifBlank { stringResource(R.string.receiver_info) },
                        style = mdMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    val isValidPhone = task.receiverMobile.length >= 10 &&
                            task.receiverMobile.all { it.isDigit() || it == '+' }
                    if (task.receiverMobile.isNotBlank() && isValidPhone) {
                        IconButton(
                            onClick = { onCallReceiver(task.receiverMobile) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Call Receiver",
                                tint = Base_white,
                                modifier = Modifier
                                    .background(
                                        CustomerAction,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Order details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = stringResource(R.string.delivery_fees), style = xsMedium)
                    Text(
                        text = stringResource(R.string.price_currency, task.deliveryPrice ?: 0.0),
                        style = lgSemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = stringResource(R.string.total_price), style = xsMedium)
                    Text(
                        text = stringResource(
                            R.string.price_currency,
                            task.collectionAmount ?: 0.0
                        ), style = lgSemiBold
                    )
                }
            }

            if (task.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.notes), style = xsMedium)
                Text(text = task.note, style = smNormal)
            }

            // Button for Pick / Deliver
            val currentStep = OrderSteps.fromStatusId(task.statusId ?: 0)
            val nextStep = when (currentStep) {
                is OrderSteps.Assigned -> OrderSteps.Picked
                is OrderSteps.Picked -> OrderSteps.Delivered
                else -> null
            }

            if (nextStep != null && onStatusChangeClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                val buttonColor = when (nextStep) {
                    is OrderSteps.Picked -> StatusPicked
                    is OrderSteps.Delivered -> StatusDelivered
                    else -> Primary
                }
                Button(
                    onClick = { onStatusChangeClick(task.orderId, nextStep) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = when (nextStep) {
                            is OrderSteps.Picked -> stringResource(R.string.confirm_pickup)
                            is OrderSteps.Delivered -> stringResource(R.string.confirm_delivery)
                            else -> ""
                        },
                        color = Base_white
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun SellerTaskItemPreview() {
    SellerTaskItem(
        task = AssignedDeliveryTask(
            orderId = 101,
            statusId = 3,
            statusName = "Assigned",
            senderMobile = "01012345678",
            fromAddress = "15 El-Tahrir St, Downtown, Cairo",
            toAddress = "Nasr City, Cairo",
            receiverMobile = "01198765432",
            deliveryPrice = 25,
            collectionAmount = 350,
            note = "Ring the bell twice"
        ),
        onCallSender = {},
        onCallReceiver = {},
    )
}
