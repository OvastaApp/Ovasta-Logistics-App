package com.ovasta.logisticsapp.presentation.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.ext.makePhoneCall
import com.ovasta.logisticsapp.base.ext.navigateToLocationClick
import com.ovasta.logisticsapp.base.ext.openWhatsApp
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.Incentives
import com.ovasta.logisticsapp.presentation.home.data.model.Milestones
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask
import com.ovasta.logisticsapp.presentation.home.presentation.components.LogoutDialog
import com.ovasta.logisticsapp.presentation.home.presentation.components.TasksContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val viewState by viewModel.viewState.collectAsState()
    val searchKey by viewModel.searchKey.collectAsState()
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

                        else -> Unit
                    }
                    viewModel.clearTasksItemActions()
                }
        }

        TasksContent(
            viewState = viewState,
            searchKey = searchKey ?: "",
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

    }
}

private fun fakeHomeViewState(): HomeViewState {
    return HomeViewState(
        sellerTasks = listOf(
            SellerTask(
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
                createdAt = "2026-04-01T10:00:00Z",
                updatedAt = "2026-04-01T12:00:00Z"
            ),
             SellerTask(
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
                createdAt = "2026-04-02T14:30:00Z",
                updatedAt = "2026-04-02T15:00:00Z"
            ),
             SellerTask(
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
                createdAt = "2026-04-03T09:15:00Z",
                updatedAt = "2026-04-03T10:45:00Z"
            ),
        ),
        filteredTasks = listOf(
            HomeTask(
                taskId = 1,
                statusId = 1,
                statusName = "in-progress",
                totalPrice = 250f,
                clientPhone = "01012345678",
                customerAddress = "Nasr City, Cairo",
                clientLang = 30.0444,
                clientLat = 31.2357
            ),
            HomeTask(
                taskId = 3,
                statusId = 2,
                statusName = "issued",
                totalPrice = 250f,
                clientPhone = "01012345678",
                customerAddress = "Nasr City, Cairo",
                clientLang = 30.0444,
                clientLat = 31.2357
            ),
            HomeTask(
                taskId = 2,
                statusId = 3,
                statusName = "pending",
                totalPrice = 480f,
                clientPhone = "01198765432",
                customerAddress = "Maadi, Cairo",
                clientLang = 29.9602,
                clientLat = 31.2569
            )
        ),
        showToastMessage = null
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TasksContentPreview() {
    TasksContent(
        viewState = fakeHomeViewState(),
        searchKey = "",
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