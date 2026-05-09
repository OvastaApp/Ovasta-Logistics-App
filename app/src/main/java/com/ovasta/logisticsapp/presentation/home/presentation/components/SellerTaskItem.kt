package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.*
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask

@Composable
fun SellerTaskItem(
    task: SellerTask,
    currency: String,
    onCallSeller: (String) -> Unit,
    onNavigateToSeller: (Double, Double) -> Unit,
    onCallCustomer: (String) -> Unit,
    onNavigateToCustomer: (Double, Double) -> Unit,
    onClick: () -> Unit
) {
    val isPicked = task.statusId >= OrderSteps.toStatus(OrderSteps.Picked)
    val statusColor = when (OrderSteps.fromStatusId(task.statusId)) {
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
                Text(
                    text = "#${task.orderId}",
                    style = mdSemiBold
                )
                Text(
                    text = task.statusName,
                    style = xsMedium.copy(color = statusColor),
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Seller Info Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SellerBackground, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.seller_info),
                        style = xsMedium.copy(color = SellerLabel)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = SellerText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = task.sellerName, style = smSemiBold.copy(color = SellerText))
                    }
                    if (!task.sellerAddress.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                tint = SellerText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = task.sellerAddress,
                                style = smNormal.copy(color = SellerText),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (!task.sellerMobile.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = SellerText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = task.sellerMobile,
                                style = smNormal.copy(color = SellerText)
                            )
                        }
                    }
                }
                if (!task.sellerMobile.isNullOrBlank()) {
                    IconButton(
                        onClick = { onCallSeller(task.sellerMobile) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call Seller",
                            tint = Base_white,
                            modifier = Modifier
                                .background(SellerAction, RoundedCornerShape(20.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Customer Info Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isPicked) CustomerBackground else CustomerBackgroundDisabled,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.customer_info),
                        style = xsMedium.copy(
                            color = if (isPicked) CustomerLabel else Gray500
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isPicked) {
                        if (!task.customerName.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = CustomerText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.customerName,
                                    style = smSemiBold.copy(color = CustomerText)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (!task.clientPhone.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = CustomerText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.clientPhone,
                                    style = smNormal.copy(color = CustomerText)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (!task.customerAddress.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    tint = CustomerText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.customerAddress,
                                    style = smNormal.copy(color = CustomerText),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Gray500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.customer_data_hidden),
                                style = smMedium.copy(color = Gray600)
                            )
                        }
                    }
                }

                if (isPicked && !task.clientPhone.isNullOrBlank()) {
                    IconButton(
                        onClick = { onCallCustomer(task.clientPhone) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call Customer",
                            tint = Base_white,
                            modifier = Modifier
                                .background(CustomerAction, RoundedCornerShape(20.dp))
                                .padding(8.dp)
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
                    Text(text = stringResource(R.string.total_price), style = xsMedium)
                    Text(text = "${task.totalPrice} $currency", style = smSemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = stringResource(R.string.delivery_fees), style = xsMedium)
                    Text(text = "${task.deliveryFees} $currency", style = smMedium)
                }
            }

            if (!task.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.notes), style = xsMedium)
                Text(text = task.notes, style = smNormal)
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun SellerTaskItemPreview() {
    SellerTaskItem(
        task = SellerTask(
            orderId = 101,
            sellerId = 1,
            sellerName = "Shop ABC",
            sellerMobile = "01012345678",
            sellerAddress = "15 El-Tahrir St, Downtown, Cairo",
            statusId = 3,
            statusName = "Assigned",
            customerName = "Ahmed",
            customerAddress = "Nasr City, Cairo",
            clientPhone = "01198765432",
            totalPrice = 350f,
            deliveryFees = 25.0,
            itemsCount = 3,
            notes = "Ring the bell twice"
        ),
        currency = "EGP",
        onCallSeller = {},
        onNavigateToSeller = { _, _ -> },
        onCallCustomer = {},
        onNavigateToCustomer = { _, _ -> },
        onClick = {}
    )
}
