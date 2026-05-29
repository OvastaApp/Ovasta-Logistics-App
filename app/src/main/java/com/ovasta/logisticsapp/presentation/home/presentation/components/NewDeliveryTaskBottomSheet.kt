package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.White
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryTaskBottomSheet(
    task: DeliveryTask,
    currency: String,
    taskAlertTimestamps: Map<Int, Long>,
    onAccept: (Int) -> Unit,
    onMinimize: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false }
    )

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState,
        properties = ModalBottomSheetDefaults.properties(
            shouldDismissOnBackPress = false
        ),
        dragHandle = null,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.new_delivery_tasks),
                        style = mdSemiBold
                    )
                    Text(
                        text = " #${task.orderId}",
                        style = mdSemiBold.copy(color = Primary)
                    )
                }
                IconButton(onClick = { onMinimize() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Gray500
                    )
                }
            }

            val startTime = taskAlertTimestamps[task.orderId] ?: System.currentTimeMillis()
            DeliveryTaskAlertCard(
                task = task,
                currency = currency,
                alertStartTime = startTime,
                onAccept = { onAccept(task.orderId) }
            )
        }
    }
}

@Composable
private fun DeliveryTaskAlertCard(
    task: DeliveryTask,
    currency: String,
    alertStartTime: Long,
    onAccept: () -> Unit
) {
    var progress by remember(task.orderId) {
        val elapsed = (System.currentTimeMillis() - alertStartTime) / 1000f
        mutableFloatStateOf(((30f - elapsed) / 30f).coerceIn(0f, 1f))
    }

    LaunchedEffect(task.orderId) {
        while (progress > 0f) {
            delay(500)
            val elapsed = (System.currentTimeMillis() - alertStartTime) / 1000f
            progress = ((30f - elapsed) / 30f).coerceIn(0f, 1f)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Direction: origin and destination with vertical line
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Primary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = task.fromAddress,
                        style = smNormal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .width(2.dp)
                        .height(16.dp)
                        .background(Gray500.copy(alpha = 0.4f))
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Gray500)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = task.toAddress,
                        style = smNormal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delivery_fees),
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.price_currency, task.deliveryPrice ?: 0.0),
                        style = lgSemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_total_price),
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    val totalPrice = (task.collectionAmount ?: 0.0) + (task.deliveryPrice ?: 0.0)
                    Text(
                        text = stringResource(
                            R.string.price_currency,
                            totalPrice
                        ), style = lgSemiBold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            if (task.note.isNotBlank()) {
                Text(
                    text = "${stringResource(R.string.notes)}: ${task.note}",
                    style = smNormal
                )
            }
            Spacer(Modifier.height(12.dp))
            // Accept button with progress drain
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gray500.copy(alpha = 0.2f))
                    .clickable { onAccept() },
                contentAlignment = Alignment.Center
            ) {
                // Draining progress background
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progress)
                        .align(Alignment.CenterStart)
                        .background(Primary)
                )
                Text(
                    stringResource(R.string.accept_order),
                    style = smMedium.copy(color = Base_white),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeliveryTaskBottomSheet() {
    NewDeliveryTaskBottomSheet(
        currency = "EGP",
        onAccept = {},
        onMinimize = {},
        taskAlertTimestamps = mapOf(1 to System.currentTimeMillis() - 10_000),
        task = DeliveryTask(
            orderId = 1,
            statusId = 1,
            statusName = "in-progress",
            senderMobile = "01012345678",
            fromAddress = "Nasr City, Cairo",
            toAddress = "Maadi, Cairo",
            receiverMobile = "01198765432",
            deliveryPrice = 50.0,
            collectionAmount = 250.0,
            note = "Handle with care",
            createdAt = null,
            updatedAt = null
        )
    )
}
