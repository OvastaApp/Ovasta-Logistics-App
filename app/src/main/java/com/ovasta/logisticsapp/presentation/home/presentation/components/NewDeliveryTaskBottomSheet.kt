package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryTaskBottomSheet(
    tasks: List<DeliveryTask>, currency: String, onAccept: (Int) -> Unit, onDismiss: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { /* Do nothing — user must explicitly accept or dismiss each task */ },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.new_delivery_tasks),
                style = mdSemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(tasks, key = { it.orderId }) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "#${task.orderId}", style = mdSemiBold)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${stringResource(R.string.from)}: ${task.fromAddress}",
                                style = smNormal
                            )
                            Text(
                                text = "${stringResource(R.string.to)}: ${task.toAddress}",
                                style = smNormal
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${stringResource(R.string.delivery_fees)}: ${task.deliveryPrice} $currency",
                                    style = xsMedium
                                )
                                Text(
                                    "${stringResource(R.string.total_price)}: ${task.collectionAmount} $currency",
                                    style = xsMedium
                                )
                            }
                            if (task.note.isNotBlank()) {
                                Text(
                                    text = "${stringResource(R.string.notes)}: ${task.note}",
                                    style = smNormal
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onDismiss(task.orderId) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.dismiss))
                                }
                                Button(
                                    onClick = { onAccept(task.orderId) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(stringResource(R.string.accept_order))
                                }
                            }
                        }
                    }
                }
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
        onDismiss = {},
        tasks = listOf(
            DeliveryTask(
                orderId = 1,
                statusId = 1,
                statusName = "in-progress",
                senderMobile = "01012345678",
                fromAddress = "Nasr City, Cairo",
                toAddress = "Maadi, Cairo",
                receiverMobile = "01198765432",
                deliveryPrice = 250,
                collectionAmount = 0,
                note = "Handle with care",
                createdAt = null,
                updatedAt = null
            )
        )
    )
}
