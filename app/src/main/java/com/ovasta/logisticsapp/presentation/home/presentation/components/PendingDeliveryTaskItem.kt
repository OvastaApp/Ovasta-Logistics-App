package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.*
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask

@Composable
fun PendingDeliveryTaskItem(
    task: DeliveryTask,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Base_white)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Order ID
            Text(text = "#${task.orderId}", style = mdSemiBold.copy(color = Primary))

            Spacer(modifier = Modifier.height(12.dp))

            // Direction: origin and destination with vertical line
            Column(modifier = Modifier.fillMaxWidth()) {
                // Origin row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.fromAddress,
                        style = smNormal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Vertical line
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .width(2.dp)
                        .height(16.dp)
                        .background(Gray500.copy(alpha = 0.4f))
                )
                // Destination row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Gray500)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.toAddress,
                        style = smNormal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delivery fees + Total price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delivery_fees),
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.price_currency, task.deliveryPrice ?: 0.0),
                            style = lgSemiBold
                        )
                        Text(
                            text = stringResource(R.string.delivery_fees),
                            style = xsMedium.copy(color = Gray500)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_total_price),
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val totalPrice = (task.collectionAmount ?: 0.0) + (task.deliveryPrice ?: 0.0)

                    Column {
                        Text(
                            text = stringResource(
                                R.string.price_currency,
                                totalPrice
                            ), style = lgSemiBold
                        )
                        Text(
                            text = stringResource(R.string.total_price),
                            style = xsMedium.copy(color = Gray500)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notes
            if (task.note.isNotBlank()) {
                Text(
                    text = "${stringResource(R.string.notes)}: ${task.note}",
                    style = smNormal
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Accept button (Box-based, matching bottom sheet style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Primary)
                    .clickable { onAccept() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.accept_order),
                    style = smMedium.copy(color = Base_white)
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun PendingDeliveryTaskItemPreview() {
    PendingDeliveryTaskItem(
        task = DeliveryTask(
            orderId = 101,
            statusId = 1,
            statusName = "Pending",
            senderMobile = "01012345678",
            fromAddress = "15 El-Tahrir St, Downtown, Cairo",
            toAddress = "Nasr City, Cairo",
            receiverMobile = "01198765432",
            deliveryPrice = 25.0,
            collectionAmount = 3050.0,
            note = "Ring the bell twice"
        ),
        onAccept = {}
    )
}
