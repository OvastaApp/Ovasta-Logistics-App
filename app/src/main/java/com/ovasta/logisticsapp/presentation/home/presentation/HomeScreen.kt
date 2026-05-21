package com.ovasta.logisticsapp.presentation.home.presentation

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.ext.makePhoneCall
import com.ovasta.logisticsapp.base.ext.navigateToLocationClick
import com.ovasta.logisticsapp.base.ext.openWhatsApp
import com.ovasta.logisticsapp.presentation.home.data.model.Incentives
import com.ovasta.logisticsapp.presentation.home.data.model.Milestones
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.presentation.components.LogoutDialog
import com.ovasta.logisticsapp.presentation.home.presentation.components.NewDeliveryTaskBottomSheet
import com.ovasta.logisticsapp.presentation.home.presentation.components.TasksContent
import com.ovasta.logisticsapp.presentation.nav.AvailableTasks
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val viewState by viewModel.viewState.collectAsState()
    val currency by viewModel.currency.collectAsState()
    BaseScreen(
        viewModel = viewModel
    ) {
        LaunchedEffect(Unit) {
//            viewModel.getSellersTasks()
            viewModel.taskItemActions
                .filterNotNull()
                .collectLatest { event ->
                    when (event) {
                        is HomeItemActions.ShowTaskDetails -> {
                            viewModel.onTaskItemAction(HomeItemActions.TaskClicked(event.taskId))
                        }

                        is HomeItemActions.OpenDirection -> {
                            context.navigateToLocationClick(event.lat, event.lng)
                        }

                        is HomeItemActions.CallRetailer -> {
                            context.makePhoneCall(event.clientPhone)
                        }

                        is HomeItemActions.WhatsAppRetailer -> {
                            context.openWhatsApp(event.clientWhatsapp)
                        }

                        is HomeItemActions.NavigateToAvailableTasks -> {
                            navigator.push(AvailableTasks)
                        }

                        else -> Unit
                    }
                    viewModel.clearTasksItemActions()
                }
        }

        TasksContent(
            viewState = viewState,
            currency = currency,
            startedTaskId = viewModel.startedTaskId,
            partnerStatistics = viewState.partnerStatistics,
            onTasksScreenAction = viewModel::onTasksScreenAction,
            onTaskItemAction = viewModel::onTaskItemAction
        )

        LogoutDialog(
            viewState.isLogoutDialogVisible,
            onConfirm = {
                viewModel.onTasksScreenAction(HomeScreenActions.OnLogoutClicked)
                viewModel.onTasksScreenAction(HomeScreenActions.ChangeLogoutDialogStatus(isVisible = false))
            }, onDismiss = {
                viewModel.onTasksScreenAction(HomeScreenActions.ChangeLogoutDialogStatus(isVisible = false))
            })

        if (viewState.activeAlertTasks.isNotEmpty() && !viewState.bottomSheetMinimized && viewState.isTracking) {
            // Flashing screen overlay (like Uber)
            ScreenFlashOverlay()

            NewDeliveryTaskBottomSheet(
                tasks = viewState.activeAlertTasks,
                currency = currency,
                taskAlertTimestamps = viewModel.taskAlertTimestamps,
                onAccept = { orderId ->
                    viewModel.onTaskItemAction(HomeItemActions.AcceptDeliveryTask(orderId))
                },
                onMinimize = {
                    viewModel.onTaskItemAction(HomeItemActions.MinimizeBottomSheet)
                }
            )
        }

    }
}

private fun fakeHomeViewState(): HomeViewState {
    return HomeViewState(
        waitingDeliveryTasks = listOf(
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
            ),
            DeliveryTask(
                orderId = 2,
                statusId = 3,
                statusName = "pending",
                senderMobile = "01198765432",
                fromAddress = "Maadi, Cairo",
                toAddress = "Heliopolis, Cairo",
                receiverMobile = "01012345678",
                deliveryPrice = 480,
                collectionAmount = 0,
                note = "Fragile item",
                createdAt = null,
                updatedAt = null
            ),
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
        showToastMessage = null
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TasksContentPreview() {
    TasksContent(
        viewState = fakeHomeViewState(),
        currency = "EGP",
        startedTaskId = -1,
        partnerStatistics = PartnerStatistics(
            withdrawTransactionsSum = 1500.0,
            deliveryProfitSum = 3200.0,
            ordersCount = 25,
            incentives = Incentives(
                month = "2026-04",
                completedOrders = 25,
                totalDeliveryValue = 3200.0,
                highestAchievedMilestone = "bronze",
                currentBonusPercentage = 3.0,
                currentBonusAmount = 96.0,
                milestones = arrayListOf(
                    Milestones(
                        targetOrders = 20,
                        bonusPercentage = 3.0,
                        realPercentage = 3.0,
                        isAchieved = true,
                        remainingOrders = 0,
                        progressPercentage = 100.0,
                        bonusAmount = 96.0
                    ),
                    Milestones(
                        targetOrders = 40,
                        bonusPercentage = 5.0,
                        realPercentage = 5.0,
                        isAchieved = false,
                        remainingOrders = 15,
                        progressPercentage = 62.5,
                        bonusAmount = 0.0
                    )
                )
            )
        ),
        onTasksScreenAction = {},
        onTaskItemAction = {}
    )
}

@Composable
private fun ScreenFlashOverlay() {
    val context = LocalContext.current
    val activity = context as? Activity

    // Wake screen and keep it on while alert is active
    DisposableEffect(Unit) {
        activity?.window?.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity?.setTurnScreenOn(true)
            activity?.setShowWhenLocked(true)
        } else {
            @Suppress("DEPRECATION")
            activity?.window?.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                activity?.setTurnScreenOn(false)
                activity?.setShowWhenLocked(false)
            } else {
                @Suppress("DEPRECATION")
                activity?.window?.clearFlags(
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                )
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "flash")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flashAlpha"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow.copy(alpha = alpha))
    )
}