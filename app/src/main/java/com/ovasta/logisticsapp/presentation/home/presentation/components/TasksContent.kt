package com.ovasta.logisticsapp.presentation.home.presentation.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.components.sharedComposable.ToastMsg
import com.ovasta.logisticsapp.base.mdRegular
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.presentation.HomeItemActions
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreenActions
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent(
    viewState: HomeViewState,
    currency: String,
    startedTaskId: Int,
    partnerStatistics: PartnerStatistics?,
    onTasksScreenAction: (HomeScreenActions) -> Unit,
    onTaskItemAction: (HomeItemActions) -> Unit,
) {
    val showToast = remember { mutableStateOf(false) }
    val toastText = viewState.showToastMessage?.let { stringResource(it) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewState.showToastMessage) {
        if (viewState.showToastMessage != null) {
            showToast.value = true
            delay(2000)
            showToast.value = false
            onTasksScreenAction(HomeScreenActions.ClearToastMessage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenteredTextAppBar(
                    title = stringResource(R.string.home, Modifier.testTag("title")),
                    showBackButton = false,
                    actions = {
                        IconButton(onClick = {
                            onTasksScreenAction(HomeScreenActions.ChangeLogoutDialogStatus(isVisible = true))
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_logout),
                                contentDescription = "Logout",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
        ) { padding ->
            val listState = rememberLazyListState()

            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        onTasksScreenAction(HomeScreenActions.RefreshTasks)
                        delay(1000)
                        isRefreshing = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Gray100)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("tasksList")
                        .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Tracking toggle
                    item(key = "tracking") {
                        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
                        TrackingToggleCard(
                            isTracking = viewState.isTracking,
                            onToggle = { onTasksScreenAction(HomeScreenActions.ToggleTracking) }
                        )
                    }

                    // Partner statistics
                    if (partnerStatistics != null && viewState.isTracking) {
                        item(key = "statistics") {
                            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                PartnerStatisticsSection(
                                    statistics = partnerStatistics,
                                    selectedMonth = viewState.monthFilter,
                                    selectedYear = viewState.yearFilter,
                                    onMonthYearChanged = { month, year ->
                                        onTasksScreenAction(
                                            HomeScreenActions.OnMonthYearFilterChanged(
                                                month,
                                                year
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    } else if (!viewState.isTracking) {
                        item(key = "statistics_hint") {
                            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                            StatisticsTrackingHint()
                        }
                    }

                    // Summary card for available orders (minimized or expired)
                    val availableCount = if (viewState.bottomSheetMinimized) {
                        viewState.activeAlertTasks.size + viewState.expiredWaitingTasks.size
                    } else {
                        viewState.expiredWaitingTasks.size
                    }

                    if (availableCount > 0) {
                        item(key = "available_orders_card") {
                            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                            AvailableOrdersSummaryCard(
                                count = availableCount,
                                onClick = { onTaskItemAction(HomeItemActions.NavigateToAvailableTasks) }
                            )
                        }
                    }

                    // Unified assigned list (appTasks + assignedDeliveryTasks)
                    val hasAppTasks = viewState.appTasks.isNotEmpty()
                    val hasDeliveryTasks = viewState.assignedDeliveryTasks.isNotEmpty()

                    if (hasAppTasks || hasDeliveryTasks) {
                        item(key = "tasks_header") {
                            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                            Text(
                                text = stringResource(R.string.tasks),
                                style = lgSemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // App tasks (HomeTask items)
                        items(viewState.appTasks, key = { "app_${it.taskId}" }) { task ->
                            TaskCard(
                                homeTask = task,
                                currency = currency,
                                startedTaskId = startedTaskId,
                                onTaskDetailsClick = { taskId, retailerId ->
                                    onTaskItemAction(HomeItemActions.ShowTaskDetails(taskId, retailerId))
                                },
                                onDirectionClick = { lat, lng ->
                                    onTaskItemAction(HomeItemActions.OpenDirection(lat, lng))
                                },
                                onContactClick = {
                                    onTaskItemAction(HomeItemActions.OpenContactBottomSheet(task))
                                },
                                onWhatsAppClick = { whatsapp ->
                                    onTaskItemAction(HomeItemActions.WhatsAppRetailer(whatsapp))
                                },
                                onClick = {
                                    onTaskItemAction(HomeItemActions.TaskClicked(task.taskId))
                                }
                            )
                        }

                        // Assigned delivery tasks
                        items(viewState.assignedDeliveryTasks, key = { "delivery_${it.orderId}" }) { task ->
                            SellerTaskItem(
                                task = task,
                                currency = currency,
                                onCallSender = { phone ->
                                    onTaskItemAction(HomeItemActions.CallRetailer(phone))
                                },
                                onCallReceiver = { phone ->
                                    onTaskItemAction(HomeItemActions.CallRetailer(phone))
                                },
                                onClick = {
                                    onTaskItemAction(HomeItemActions.TaskClicked(task.orderId))
                                }
                            )
                        }
                    }

                    if (!hasAppTasks && !hasDeliveryTasks) {
                        item(key = "empty") {
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
                    }

                    item { Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp))) }
                }
            }
        }
        if (showToast.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = dimensionResource(com.intuit.sdp.R.dimen._60sdp),
                        start = dimensionResource(com.intuit.sdp.R.dimen._60sdp),
                        end = dimensionResource(com.intuit.sdp.R.dimen._60sdp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (toastText != null) {
                    ToastMsg(text = toastText)
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun TasksContentPreview() {
    TasksContent(
        viewState = HomeViewState(
            waitingDeliveryTasks = listOf(
                DeliveryTask(
                    orderId = 3,
                    statusId = 2,
                    statusName = "issued",
                    senderMobile = "01012345678",
                    fromAddress = "Nasr City, Cairo",
                    toAddress = "Zamalek, Cairo",
                    receiverMobile = "01198765432",
                    deliveryPrice = 250,
                    collectionAmount = 0,
                    note = "",
                    createdAt = null,
                    updatedAt = null
                ),
            ),
            isTracking = false,
            showToastMessage = null
        ),
        currency = "EGP",
        startedTaskId = -1,
        partnerStatistics = null,
        onTasksScreenAction = {},
        onTaskItemAction = {}
    )
}