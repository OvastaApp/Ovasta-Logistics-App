package com.ovasta.logisticsapp.presentation.home.presentation.components

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.confirm
import com.ovasta.logisticsapp.shared.resources.create_delivery_order
import com.ovasta.logisticsapp.shared.resources.home
import com.ovasta.logisticsapp.shared.resources.ic_add
import com.ovasta.logisticsapp.shared.resources.no_tasks_available
import com.ovasta.logisticsapp.shared.resources.sender_info
import com.ovasta.logisticsapp.shared.resources.tasks
import com.ovasta.logisticsapp.shared.resources.update_delivery_fees_message
import com.ovasta.logisticsapp.shared.resources.update_delivery_fees_title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.constants.LocalConstants
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.components.sharedComposable.ToastMsg
import com.ovasta.logisticsapp.base.mdRegular
import com.ovasta.logisticsapp.presentation.home.data.model.AssignedDeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.presentation.HomeItemActions
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreenActions
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.ovasta.logisticsapp.base.ext.sdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent(
    viewState: HomeViewState,
    currency: String,
    startedTaskId: Int,
    partnerStatistics: PartnerStatistics?,
    onTasksScreenAction: (HomeScreenActions) -> Unit,
    onTaskItemAction: (HomeItemActions) -> Unit,
    // Checked before opening an order action's dialog / sheet; the ViewModel re-checks on submit
    ensureLocationTracking: () -> Boolean = { true },
) {
    val showToast = remember { mutableStateOf(false) }
    val toastText = viewState.showToastMessage?.let { stringResource(it) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    var confirmDialogState by remember { mutableStateOf<Pair<Int, OrderSteps>?>(null) }
    var editFeesState by remember { mutableStateOf<AssignedDeliveryTask?>(null) }
    var showCreateOrderDialog by remember { mutableStateOf(false) }

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
            contentWindowInsets = WindowInsets(0, 0, 0, 0), topBar = {
                CenteredTextAppBar(
                    title = viewState.userName.ifBlank { stringResource(Res.string.home) },
                    showBackButton = false
                )
            },
            floatingActionButton = {
                // Outsource agents can't create orders; hidden until we know which they are
                if (viewState.isTracking && viewState.isOutsource == false) {
                    FloatingActionButton(
                        onClick = { if (ensureLocationTracking()) showCreateOrderDialog = true },
                        containerColor = Primary,
                        contentColor = Base_white
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = stringResource(Res.string.create_delivery_order)
                        )
                    }
                }
            }) { padding ->
            val listState = rememberLazyListState()

            PullToRefreshBox(
                state = pullToRefreshState, isRefreshing = isRefreshing, onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        onTasksScreenAction(HomeScreenActions.RefreshTasks)
                        delay(1000)
                        isRefreshing = false
                    }
                }, modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Gray100)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("tasksList")
                        .padding(horizontal = 12.sdp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Tracking toggle
                    item(key = "tracking") {
                        Spacer(modifier = Modifier.height(12.sdp))
                        TrackingToggleCard(
                            isTracking = viewState.isTracking,
                            onToggle = { onTasksScreenAction(HomeScreenActions.ToggleTracking) })
                    }

                    // Wallet warning from `home`, shown whether tracking or not
                    viewState.walletWarning?.let { warning ->
                        item(key = "wallet_warning") {
                            Spacer(modifier = Modifier.height(8.sdp))
                            WalletWarningCard(message = warning)
                        }
                    }

                    // Partner statistics
                    if (partnerStatistics != null && viewState.isTracking) {
                        item(key = "statistics") {
                            Spacer(modifier = Modifier.height(8.sdp))
                            PartnerStatisticsSection(
                                statistics = partnerStatistics,
                                selectedMonth = viewState.monthFilter,
                                selectedYear = viewState.yearFilter,
                                onMonthYearChanged = { month, year ->
                                    onTasksScreenAction(
                                        HomeScreenActions.OnMonthYearFilterChanged(
                                            month, year
                                        )
                                    )
                                })
                        }
                    } else if (!viewState.isTracking && viewState.isOutsource == false) {
                        item(key = "statistics_hint") {
                            Spacer(modifier = Modifier.height(8.sdp))
                            StatisticsTrackingHint()
                        }
                    }

                    // Only show orders and tasks if tracking is enabled
                    if (viewState.isTracking) {
                        // Summary card for available orders (all waiting tasks from Firebase)
                        val availableCount = viewState.waitingDeliveryTasks.size
                        val shouldShowSummary =
                            viewState.bottomSheetMinimized || (viewState.currentAlertTask == null && availableCount > 0)

                        if (viewState.isAvailableTasksLoading) {
                            item(key = "available_orders_shimmer") {
                                Spacer(modifier = Modifier.height(8.sdp))
                                ShimmerAvailableOrdersCard()
                            }
                        } else if (shouldShowSummary && availableCount > 0) {
                            item(key = "available_orders_card") {
                                Spacer(modifier = Modifier.height(8.sdp))
                                AvailableOrdersSummaryCard(
                                    count = availableCount,
                                    onClick = { onTaskItemAction(HomeItemActions.NavigateToAvailableTasks) })
                            }
                        }

                        // Unified assigned list (appTasks + assignedDeliveryTasks)
                        val hasAppTasks = viewState.appTasks.isNotEmpty()
                        val hasDeliveryTasks = viewState.assignedDeliveryTasks.isNotEmpty()

                        if (viewState.isTasksLoading) {
                            item(key = "tasks_shimmer") {
                                Spacer(modifier = Modifier.height(8.sdp))
                                ShimmerTasksList()
                            }
                        } else if (hasAppTasks || hasDeliveryTasks) {
                            item(key = "tasks_header") {
                                Spacer(modifier = Modifier.height(8.sdp))
                                Text(
                                    text = stringResource(Res.string.tasks),
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
                                        onTaskItemAction(
                                            HomeItemActions.ShowTaskDetails(
                                                taskId, retailerId
                                            )
                                        )
                                    },
                                    onDirectionClick = { lat, lng ->
                                        onTaskItemAction(HomeItemActions.OpenDirection(lat, lng))
                                    },
                                    // Calls the client directly; the contact sheet this used to open is gone
                                    onContactClick = { phone ->
                                        onTaskItemAction(HomeItemActions.CallRetailer(phone))
                                    },
                                    onWhatsAppClick = { whatsapp ->
                                        onTaskItemAction(HomeItemActions.WhatsAppRetailer(whatsapp))
                                    },
                                    onStatusChangeClick = if (LocalConstants.ORDER_ACTIONS_ENABLED) {
                                        { orderId, status ->
                                            if (ensureLocationTracking()) confirmDialogState = Pair(orderId, status)
                                        }
                                    } else null)
                            }

                            // Assigned delivery tasks
                            // Fall back to the index so tasks with a null order id don't share a key
                            itemsIndexed(
                                viewState.assignedDeliveryTasks,
                                key = { index, task -> "delivery_${task.orderId ?: "index_$index"}" }) { _, task ->
                                SellerTaskItem(task = task, onCallSender = { phone ->
                                    onTaskItemAction(HomeItemActions.CallRetailer(phone))
                                }, onCallReceiver = { phone ->
                                    onTaskItemAction(HomeItemActions.CallRetailer(phone))
                                }, onStatusChangeClick = { orderId, status ->
                                    if (ensureLocationTracking()) confirmDialogState = Pair(orderId, status)
                                }, onEditFeesClick = { editTask ->
                                    if (ensureLocationTracking()) editFeesState = editTask
                                })
                            }
                        }

                        if (!hasAppTasks && !hasDeliveryTasks && !viewState.isTasksLoading) {
                            item(key = "empty") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 90.sdp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = stringResource(Res.string.no_tasks_available),
                                        style = mdRegular.copy(color = Gray500)
                                    )
                                }
                            }
                        }
                    }

                    // Extra bottom space so the FAB doesn't overlap the last item
                    item { Spacer(modifier = Modifier.height(80.sdp)) }
                }
            }
        }
        if (showToast.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = 60.sdp,
                        start = 60.sdp,
                        end = 60.sdp
                    ), contentAlignment = Alignment.Center
            ) {
                if (toastText != null) {
                    ToastMsg(text = toastText)
                }
            }
        }
        confirmDialogState?.let { (orderId, status) ->
            ConfirmStatusDialog(status = status, onConfirm = {
                onTaskItemAction(HomeItemActions.ChangeOrderStatus(orderId, status))
                confirmDialogState = null
            }, onDismiss = { confirmDialogState = null })
        }

        editFeesState?.let { task ->
            // The edit button is hidden for tasks without an order id, so this is only a type guard
            val orderId = task.orderId ?: return@let
            DeliveryFeesDialog(
                title = stringResource(Res.string.update_delivery_fees_title),
                message = stringResource(
                    Res.string.update_delivery_fees_message,
                    orderId,
                    task.fromAddress.orEmpty().ifBlank { stringResource(Res.string.sender_info) }
                ),
                maxValue = 9999.0,
                confirmText = stringResource(Res.string.confirm),
                initialValue = task.deliveryPrice,
                onConfirm = { fees ->
                    onTaskItemAction(HomeItemActions.UpdateDeliveryFees(orderId, fees))
                    editFeesState = null
                },
                onDismiss = { editFeesState = null }
            )
        }

        if (showCreateOrderDialog) {
            CreateDeliveryOrderBottomSheet(
                onConfirm = { deliveryPrice, receiverMobile, toAddress, note ->
                    onTasksScreenAction(
                        HomeScreenActions.CreateDeliveryOrder(
                            deliveryPrice = deliveryPrice,
                            receiverMobile = receiverMobile,
                            toAddress = toAddress,
                            note = note,
                        )
                    )
                    showCreateOrderDialog = false
                },
                onDismiss = { showCreateOrderDialog = false }
            )
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
                deliveryPrice = 250.0,
                collectionAmount = 101.00,
                note = "",
                createdAt = null,
                updatedAt = null
            ),
        ), isTracking = false, showToastMessage = null
    ),
        currency = "EGP",
        startedTaskId = -1,
        partnerStatistics = null,
        onTasksScreenAction = {},
        onTaskItemAction = {})
}