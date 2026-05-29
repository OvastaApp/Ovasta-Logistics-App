package com.ovasta.logisticsapp.presentation.home.presentation.availableTasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.mdRegular
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.presentation.components.PendingDeliveryTaskItem

@Composable
fun AvailableTasksScreen(viewModel: AvailableTasksViewModel, onOrderAccepted: () -> Unit = {}) {
    val navigator = LocalNavigator.current
    val tasks by viewModel.tasks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.popBackEvent.collect {
            onOrderAccepted()
            navigator.pop()
        }
    }

    BaseScreen(viewModel = viewModel) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenteredTextAppBar(
                    title = stringResource(R.string.available_orders),
                    showBackButton = true,
                    onBackButtonPressed = { navigator.pop() }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Gray100)
                    .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = dimensionResource(com.intuit.sdp.R.dimen._90sdp)),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = stringResource(R.string.no_tasks_available),
                                style = mdRegular.copy(color = Gray500)
                            )
                        }
                    }
                } else {
                    items(tasks, key = { it.orderId }) { task ->
                        PendingDeliveryTaskItem(
                            task = task,
                            onAccept = { viewModel.acceptOrder(task.orderId) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AvailableTasksScreenPreview() {
    val fakeTasks = listOf(
        DeliveryTask(
            orderId = 1,
            statusId = 1,
            statusName = "pending",
            senderMobile = "01012345678",
            fromAddress = "Nasr City, Cairo",
            toAddress = "Maadi, Cairo",
            receiverMobile = "01198765432",
            deliveryPrice = 250.0,
            collectionAmount = 5000.0,
            note = "Handle with care"
        ),
        DeliveryTask(
            orderId = 2,
            statusId = 1,
            statusName = "pending",
            senderMobile = "01198765432",
            fromAddress = "Heliopolis, Cairo",
            toAddress = "Zamalek, Cairo",
            receiverMobile = "01012345678",
            deliveryPrice = 350.0,
            collectionAmount = 800.0,
            note = ""
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = "Available Orders",
                showBackButton = true,
                onBackButtonPressed = {}
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Gray100)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fakeTasks, key = { it.orderId }) { task ->
                PendingDeliveryTaskItem(
                    task = task,
                    onAccept = {}
                )
            }
        }
    }
}