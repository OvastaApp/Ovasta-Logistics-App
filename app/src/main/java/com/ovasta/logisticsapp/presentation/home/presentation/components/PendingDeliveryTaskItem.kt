package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
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
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask

@Composable
fun PendingDeliveryTaskItem(
    task: DeliveryTask,
    currency: String,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Base_white)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Order ID
            Text(text = "#${task.orderId}", style = mdSemiBold)

            Spacer(modifier = Modifier.height(12.dp))

            // From address
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SellerAction,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(text = stringResource(R.string.from), style = xsMedium.copy(color = Gray500))
                    Text(
                        text = task.fromAddress,
                        style = smNormal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // To address
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    tint = CustomerAction,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(text = stringResource(R.string.to), style = xsMedium.copy(color = Gray500))
                    Text(
                        text = task.toAddress,
                        style = smNormal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Delivery fees + Total price
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

            Spacer(modifier = Modifier.height(12.dp))

            // Accept button
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SellerAction)
            ) {
                Text(text = stringResource(R.string.accept_order), color = Base_white)
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
            deliveryPrice = 25,
            collectionAmount = 350,
            note = "Ring the bell twice"
        ),
        currency = "EGP",
        onAccept = {}
    )
}
