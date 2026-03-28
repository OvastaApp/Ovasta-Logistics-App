package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.components.sharedComposable.NavigationAction
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smSemiBold
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlin.text.ifEmpty

@Composable
fun TaskCard(
    homeTask: HomeTask,
    currency: String,
    startedTaskId: Int,
    onTaskDetailsClick: (taskId: Int, retailerId: Int) -> Unit,
    onDirectionClick: (Float, Float) -> Unit,
    onContactClick: (String) -> Unit,
    onWhatsAppClick: (String) -> Unit,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        colors = CardDefaults.cardColors(containerColor = Base_white),
        border = BorderStroke(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            .testTag("taskCard_${homeTask.taskId}")
    ) {
        Column(
            modifier = Modifier.padding(
                all = dimensionResource(com.intuit.sdp.R.dimen._14sdp)
            )
        ) {
            // Header: Task ID + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoRow(
                    icon = R.drawable.ic_hash,
                    label = homeTask.taskId.toString(),
                    textStyle = smSemiBold.copy(color = Gray800),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("taskId"),
                    maxLines = 1
                )

                StatusTag(
                    statusId = homeTask.statusId,
                    statusName = homeTask.statusName,
                    modifier = Modifier.testTag("taskStatus")
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))

            // Customer name (if available)
            if (!homeTask.customerName.isNullOrBlank()) {
                Text(
                    text = homeTask.customerName,
                    style = mdMedium.copy(color = Gray800),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(com.intuit.sdp.R.dimen._24sdp)
                        )
                        .testTag("customerName")
                )
                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
            }

            InfoRow(
                icon = R.drawable.ic_address,
                label = homeTask.customerAddress?.ifEmpty { stringResource(R.string.no_address) }
                    ?: stringResource(R.string.no_address),
                modifier = Modifier.testTag("clientAddress")
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))

            // Products + Price in a single row to save space
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoRow(
                    icon = R.drawable.ic_count,
                    label = stringResource(R.string.products_count, homeTask.itemsCount.toString()),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("productCount"),
                    maxLines = 1
                )

                InfoRow(
                    icon = R.drawable.ic_price,
                    label = String.format(
                        stringResource(R.string.price_currency),
                        homeTask.totalPrice.toString(),
                        currency
                    ),
                    textStyle = mdMedium.copy(color = Gray800),
                    modifier = Modifier.testTag("totalPrice"),
                    maxLines = 1
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp)),
                thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
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
                onWhatsAppClick = { onWhatsAppClick(homeTask.clientPhone ?: "") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskCard() {
    TaskCard(
        homeTask = HomeTask(
            taskId = 12345,
            statusId = 1,
            statusName = "Pending",
            customerName = "Ahmed Mohamed",
            customerAddress = "123 Main St, City",
            itemsCount = 5,
            totalPrice = 150.0f,
            clientLat = 37.7749f,
            clientLang = -122.4194f,
            clientPhone = "1234567890"
        ),
        currency = "$",
        startedTaskId = -1,
        onTaskDetailsClick = { _, _ -> },
        onDirectionClick = { _, _ -> },
        onContactClick = { _ -> },
        onWhatsAppClick = { _ -> },
        onClick = {}
    )
}