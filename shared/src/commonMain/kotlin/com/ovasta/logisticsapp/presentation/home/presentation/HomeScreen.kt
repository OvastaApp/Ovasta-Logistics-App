package com.ovasta.logisticsapp.presentation.home.presentation

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.ic_location
import com.ovasta.logisticsapp.shared.resources.location_consent_message
import com.ovasta.logisticsapp.shared.resources.location_consent_not_now
import com.ovasta.logisticsapp.shared.resources.location_consent_ok
import com.ovasta.logisticsapp.shared.resources.location_consent_title
import com.ovasta.logisticsapp.shared.resources.view_order_details_message
import com.ovasta.logisticsapp.shared.resources.view_order_details_title

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.PlatformAction
import com.ovasta.logisticsapp.base.WakeScreenEffect
import com.ovasta.logisticsapp.base.rememberPlatformActions
import com.ovasta.logisticsapp.presentation.home.data.model.Incentives
import com.ovasta.logisticsapp.presentation.home.data.model.Milestones
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseDialog
import com.ovasta.logisticsapp.base.components.sharedComposable.ConfirmDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.ovasta.logisticsapp.presentation.home.presentation.components.NewDeliveryTaskBottomSheet
import com.ovasta.logisticsapp.presentation.home.presentation.components.TasksContent
import com.ovasta.logisticsapp.presentation.nav.AvailableTasks
import com.ovasta.logisticsapp.presentation.nav.TaskDetails
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val platformActions = rememberPlatformActions()
    val navigator = LocalNavigator.current
    val viewState by viewModel.viewState.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getAssignedDeliveryOrders()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BaseScreen(
        viewModel = viewModel
    ) {
        LaunchedEffect(Unit) {
            viewModel.taskItemActions
                .filterNotNull()
                .collectLatest { event ->
                    when (event) {

                        is HomeItemActions.OpenDirection -> {
                            platformActions(PlatformAction.OpenMaps(event.lat, event.lng))
                        }

                        is HomeItemActions.CallRetailer -> {
                            platformActions(PlatformAction.Call(event.clientPhone))
                        }

                        is HomeItemActions.WhatsAppRetailer -> {
                            platformActions(PlatformAction.OpenWhatsApp(event.clientWhatsapp))
                        }

                        is HomeItemActions.NavigateToAvailableTasks -> {
                            navigator.push(AvailableTasks)
                        }

                        is HomeItemActions.ShowTaskDetails -> {
                            navigator.push(TaskDetails(event.taskId))
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
            onTaskItemAction = viewModel::onTaskItemAction,
            ensureLocationTracking = viewModel::ensureLocationTracking
        )

        viewState.orderToStart?.let { order ->
            ConfirmDialog(
                title = stringResource(Res.string.view_order_details_title),
                message = stringResource(
                    Res.string.view_order_details_message,
                    // Customer name, or the order number when the order has none
                    // Customer name, or the order number when the order has none
                    order.customerName?.takeIf { it.isNotBlank() } ?: "#${order.taskId}"
                ),
                onPrimaryClick = {
                    viewModel.onTaskItemAction(HomeItemActions.StartOrder(order.taskId))
                },
                onDismiss = { viewModel.onTaskItemAction(HomeItemActions.DismissStartOrder) }
            )
        }

        if (viewState.isLocationConsentDialogVisible) {
            BaseDialog(
                icon = painterResource(Res.drawable.ic_location),
                title = stringResource(Res.string.location_consent_title),
                message = stringResource(Res.string.location_consent_message),
                primaryButtonText = stringResource(Res.string.location_consent_ok),
                secondaryButtonText = stringResource(Res.string.location_consent_not_now),
                onPrimaryClick = { viewModel.onTasksScreenAction(HomeScreenActions.AcceptLocationConsent) },
                onSecondaryClick = { viewModel.onTasksScreenAction(HomeScreenActions.DismissLocationConsent) },
                onDismiss = { viewModel.onTasksScreenAction(HomeScreenActions.DismissLocationConsent) },
                dismissOnClickOutside = false
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
                deliveryPrice = 250.0,
                collectionAmount = 2000.0,
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
                deliveryPrice = 480.0,
                collectionAmount = 2000.0,
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
                deliveryPrice = 20.0,
                collectionAmount = 100.0,
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
fun ScreenFlashOverlay() {
    // Wake the screen and keep it on while the alert is shown
    WakeScreenEffect()

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