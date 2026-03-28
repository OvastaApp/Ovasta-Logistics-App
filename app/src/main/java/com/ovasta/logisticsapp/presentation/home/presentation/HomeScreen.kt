package com.ovasta.logisticsapp.presentation.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.ContactSheet
import com.ovasta.logisticsapp.base.ext.copyPhoneNumber
import com.ovasta.logisticsapp.base.ext.makePhoneCall
import com.ovasta.logisticsapp.base.ext.navigateToLocationClick
import com.ovasta.logisticsapp.base.ext.openWhatsApp
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.presentation.components.LogoutDialog
import com.ovasta.logisticsapp.presentation.home.presentation.components.TasksContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsState()
    val searchKey by viewModel.searchKey.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val showContactSheet by viewModel.showContactSheet.collectAsState()
    BaseScreen(
        viewModel = viewModel,
        navController = navController
    ) {
        LaunchedEffect(Unit) {
            viewModel.getAssignedTasks()

            viewModel.taskItemActions
                .filterNotNull()
                .collectLatest { event ->
                    when (event) {
                        is HomeItemActions.ShowTaskDetails -> {
                        }

                        is HomeItemActions.OpenDirection -> {
                            context.navigateToLocationClick(event.lat, event.lng)
                        }

                        is HomeItemActions.CopyPhone -> {
                            context.copyPhoneNumber(event.retailerPhone)
                        }

                        is HomeItemActions.CallRetailer -> {
                            context.makePhoneCall(event.retailerPhone)
                        }

                        is HomeItemActions.WhatsAppRetailer -> {
                            context.openWhatsApp(event.retailerPhone)
                        }

                        else -> Unit
                    }
                    viewModel.clearTasksItemActions()
                }
        }

        if (showContactSheet != null) {
            ContactSheet(
                taskId = showContactSheet?.taskId ?: 0,
                homeTaskInfo = showContactSheet!!,
                onAction = { viewModel.onTaskItemAction(it) }
            )
        }

        LogoutDialog(
            viewState.isLogoutDialogVisible,
            onConfirm = {
                viewModel.onTasksScreenAction(HomeScreenActions.OnLogoutClicked)
                viewModel.onTasksScreenAction(HomeScreenActions.ChangeLogoutDialogStatus(isVisible = false))
            }, onDismiss = {
                viewModel.onTasksScreenAction(HomeScreenActions.ChangeLogoutDialogStatus(isVisible = false))
            })
        TasksContent(
            viewState = viewState,
            searchKey = searchKey.orEmpty(),
            currency,
            viewModel.startedTaskId,
            partnerStatistics = viewState.partnerStatistics,
            onTasksScreenAction = viewModel::onTasksScreenAction,
            onTaskItemAction = viewModel::onTaskItemAction
        )
    }
}

private fun fakeHomeViewState(): HomeViewState {
    return HomeViewState(
        filteredTasks = listOf(
            HomeTask(
                taskId = 1,
                statusId = 1,
                statusName = "in-progress",
                totalPrice = 250f,
                clientPhone = "01012345678",
                customerAddress = "Nasr City, Cairo",
                clientLang = 30.0444f,
                clientLat = 31.2357f
            ),
            HomeTask(
                taskId = 3,
                statusId = 2,
                statusName = "issued",
                totalPrice = 250f,
                clientPhone = "01012345678",
                customerAddress = "Nasr City, Cairo",
                clientLang = 30.0444f,
                clientLat = 31.2357f
            ),
            HomeTask(
                taskId = 2,
                statusId = 3,
                statusName = "pending",
                totalPrice = 480f,
                clientPhone = "01198765432",
                customerAddress = "Maadi, Cairo",
                clientLang = 29.9602f,
                clientLat = 31.2569f
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
            deliveryProfitSum = 3200.50,
            ordersCount = 25,
            targetOrdersCount = 40,
            targetEndDate = "2026-04-15"
        ),
        onTasksScreenAction = {},
        onTaskItemAction = {}
    )
}