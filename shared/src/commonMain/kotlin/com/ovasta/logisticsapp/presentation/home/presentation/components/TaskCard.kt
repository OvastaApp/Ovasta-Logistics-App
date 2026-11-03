package com.ovasta.logisticsapp.presentation.home.presentation.components

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.confirm_delivery
import com.ovasta.logisticsapp.shared.resources.confirm_pickup
import com.ovasta.logisticsapp.shared.resources.ic_address
import com.ovasta.logisticsapp.shared.resources.ic_count
import com.ovasta.logisticsapp.shared.resources.ic_hash
import com.ovasta.logisticsapp.shared.resources.ic_price
import com.ovasta.logisticsapp.shared.resources.no_address
import com.ovasta.logisticsapp.shared.resources.price_currency
import com.ovasta.logisticsapp.shared.resources.products_count

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.components.sharedComposable.NavigationAction
import com.ovasta.logisticsapp.base.mdMedium
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.StatusDelivered
import com.ovasta.logisticsapp.base.StatusPicked
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlin.text.ifEmpty
import com.ovasta.logisticsapp.base.ext.sdp

@Composable
fun TaskCard(
    homeTask: HomeTask,
    currency: String,
    startedTaskId: Int,
    onTaskDetailsClick: (taskId: Int, retailerId: Int) -> Unit,
    onDirectionClick: (Double, Double) -> Unit,
    onContactClick: (String) -> Unit,
    onWhatsAppClick: (String) -> Unit,
    onStatusChangeClick: ((orderId: Int, OrderSteps) -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(8.sdp),
        colors = CardDefaults.cardColors(containerColor = Base_white),
        border = BorderStroke(
            width = 1.sdp,
            color = Gray200
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.sdp)
            .clickable { onTaskDetailsClick(homeTask.taskId, 0) }
            .testTag("taskCard_${homeTask.taskId}")
    ) {
        Column(
            modifier = Modifier.padding(
                all = 16.sdp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoRow(
                    icon = Res.drawable.ic_hash,
                    label = homeTask.taskId.toString(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("taskId")
                )

                StatusTag(
                    statusId = homeTask.statusId,
                    statusName = homeTask.statusName,
                    modifier = Modifier.testTag("taskStatus")
                )
            }

            Spacer(modifier = Modifier.height(8.sdp))

            InfoRow(
                icon = Res.drawable.ic_address,
                label = homeTask.customerAddress?.ifEmpty { stringResource(Res.string.no_address) }
                    ?: stringResource(Res.string.no_address),
                modifier = Modifier.testTag("clientAddress")
            )

            Spacer(modifier = Modifier.height(8.sdp))

            InfoRow(
                icon = Res.drawable.ic_count,
                label = stringResource(Res.string.products_count, homeTask.itemsCount.toString()),
                modifier = Modifier.testTag("productCount")
            )

            Spacer(modifier = Modifier.height(8.sdp))

            InfoRow(
                icon = Res.drawable.ic_price,
                label = stringResource(Res.string.price_currency, homeTask.totalPrice.toString(), currency),
                textStyle = mdMedium.copy(color = Gray800),
                modifier = Modifier.testTag("totalPrice")
            )

            homeTask.assignedAt?.let { assignedAt ->
                Spacer(modifier = Modifier.height(8.sdp))

                AssignedElapsedTime(
                    assignedAtMillis = assignedAt.toMilliseconds().toLong(),
                    modifier = Modifier.testTag("assignedElapsedTime")
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.sdp),
                thickness = 1.sdp,
                color = Gray200
            )

            NavigationAction(
                clickedTaskId = homeTask.taskId,
                onDirectionClick = {
                    onDirectionClick(
                        homeTask.clientLang, homeTask.clientLat
                    )
                },
                onContactClick = { onContactClick(homeTask.clientPhone ?: "") },
                onWhatsAppClick = { onWhatsAppClick(homeTask.clientWhatsapp ?: "") }
            )

            val currentStep = OrderSteps.fromStatusId(homeTask.statusId)
            val nextStep = when (currentStep) {
                is OrderSteps.Assigned -> OrderSteps.Picked
                is OrderSteps.Picked -> OrderSteps.Delivered
                else -> null
            }

            if (nextStep != null && onStatusChangeClick != null) {
                Spacer(modifier = Modifier.height(8.sdp))
                
                Button(
                    onClick = { onStatusChangeClick(homeTask.taskId, nextStep) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("statusChangeButton_${homeTask.taskId}"),
                    shape = RoundedCornerShape(8.sdp)
                ) {
                    Text(
                        text = when (nextStep) {
                            is OrderSteps.Picked -> stringResource(Res.string.confirm_pickup)
                            is OrderSteps.Delivered -> stringResource(Res.string.confirm_delivery)
                            else -> ""
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTaskCard() {
    TaskCard(
        homeTask = HomeTask(
            taskId = 12345,
            statusId = 1,
            statusName = "Pending",
            customerAddress = "123 Main St, City",
            itemsCount = 5,
            totalPrice = 150.0f,
            clientLat = 37.7749,
            clientLang = -122.4194,
            clientPhone = "1234567890",
            assignedAt = Timestamp.now()
        ),
        currency = "$",
        startedTaskId = -1,
        onTaskDetailsClick = { _, _ -> },
        onDirectionClick = { _, _ -> },
        onContactClick = { _ -> },
        onWhatsAppClick = { _ -> },
    )
}