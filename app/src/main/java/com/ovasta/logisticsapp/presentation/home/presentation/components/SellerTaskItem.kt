package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
        OrderSteps.Pending -> Color(0xFFFFA000)
        OrderSteps.Assigned -> Color(0xFF1976D2)
        OrderSteps.Picked -> Color(0xFF7B1FA2)
        OrderSteps.Delivered -> Color(0xFF388E3C)
        OrderSteps.Canceled -> Color(0xFFD32F2F)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.seller_info),
                        style = xsMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Gray600,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = task.sellerName, style = smSemiBold)
                    }
                    if (!task.sellerMobile.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = Gray600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = task.sellerMobile, style = smNormal)
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
                            tint = Color.White,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Customer Info Section
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.customer_info),
                        style = xsMedium.copy(
                            color = if (isPicked) MaterialTheme.colorScheme.primary else Gray500
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isPicked) {
                        if (!task.customerName.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Gray600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = task.customerName, style = smSemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (!task.clientPhone.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Gray600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = task.clientPhone, style = smNormal)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (!task.customerAddress.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    tint = Gray600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.customerAddress,
                                    style = smNormal,
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

                if (isPicked) {
                    Column(horizontalAlignment = Alignment.End) {
                        if (!task.clientPhone.isNullOrBlank()) {
                            IconButton(
                                onClick = { onCallCustomer(task.clientPhone) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = "Call Customer",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(8.dp)
                                )
                            }
                        }
                        if (!task.customerAddress.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            IconButton(
                                onClick = {
                                    onNavigateToCustomer(
                                        task.clientLat,
                                        task.clientLang
                                    )
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Navigate",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .background(
                                            Color(0xFF4CAF50),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Order details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
            statusId = 2,
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
