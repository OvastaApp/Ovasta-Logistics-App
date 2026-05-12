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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.*
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask

@Composable
fun SellerTaskItem(
    task: DeliveryTask,
    currency: String,
    onCallSender: (String) -> Unit,
    onCallReceiver: (String) -> Unit,
    onClick: () -> Unit
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
        onClick = onClick
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

            // Sender Info Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SellerBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.sender_info),
                        style = xsMedium.copy(color = SellerLabel)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (task.senderMobile.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = SellerText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = task.senderMobile, style = smNormal.copy(color = SellerText))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (task.fromAddress.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = SellerText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = task.fromAddress,
                                style = smNormal.copy(color = SellerText),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                if (task.senderMobile.isNotBlank()) {
                    IconButton(onClick = { onCallSender(task.senderMobile) }, modifier = Modifier.size(40.dp)) {
                        Icon(
                            Icons.Default.Call, contentDescription = "Call Sender", tint = Base_white,
                            modifier = Modifier.background(SellerAction, RoundedCornerShape(20.dp)).padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Receiver Info Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomerBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.receiver_info),
                        style = xsMedium.copy(color = CustomerLabel)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (task.receiverMobile.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = CustomerText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = task.receiverMobile, style = smNormal.copy(color = CustomerText))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (task.toAddress.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = CustomerText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = task.toAddress,
                                style = smNormal.copy(color = CustomerText),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (task.receiverMobile.isNotBlank()) {
                    IconButton(onClick = { onCallReceiver(task.receiverMobile) }, modifier = Modifier.size(40.dp)) {
                        Icon(
                            Icons.Default.Call, contentDescription = "Call Receiver", tint = Base_white,
                            modifier = Modifier.background(CustomerAction, RoundedCornerShape(20.dp)).padding(8.dp)
                        )
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
                    Text(text = "${task.deliveryPrice ?: 0} $currency", style = smMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = stringResource(R.string.total_price), style = xsMedium)
                    Text(text = "${task.collectionAmount ?: 0} $currency", style = smSemiBold)
                }
            }

            if (task.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.notes), style = xsMedium)
                Text(text = task.note, style = smNormal)
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun SellerTaskItemPreview() {
    SellerTaskItem(
        task = DeliveryTask(
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
        currency = "EGP",
        onCallSender = {},
        onCallReceiver = {},
        onClick = {}
    )
}
