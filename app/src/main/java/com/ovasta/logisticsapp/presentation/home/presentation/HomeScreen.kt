package com.ovasta.logisticsapp.presentation.home.presentation

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseDialog
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.ConfirmDialog
import com.ovasta.logisticsapp.base.components.sharedComposable.ContactSheet
import com.ovasta.logisticsapp.base.components.sharedComposable.NavigationAction
import com.ovasta.logisticsapp.base.components.sharedComposable.SearchBox
import com.ovasta.logisticsapp.base.components.sharedComposable.ShowLoadingIndicator
import com.ovasta.logisticsapp.base.components.sharedComposable.ToastMsg
import com.ovasta.logisticsapp.base.ext.FRACTION_DIGITS
import com.ovasta.logisticsapp.base.ext.copyPhoneNumber
import com.ovasta.logisticsapp.base.ext.formatPrice
import com.ovasta.logisticsapp.base.ext.makePhoneCall
import com.ovasta.logisticsapp.base.ext.navigateToLocationClick
import com.ovasta.logisticsapp.base.ext.openWhatsApp
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdRegular
import com.ovasta.logisticsapp.base.services.LocationManager
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.TaskStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import okhttp3.internal.platform.PlatformRegistry.applicationContext

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
//        viewModel.getAssignedTasks()

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
            onTasksScreenAction = viewModel::onTasksScreenAction,
            onTaskItemAction = viewModel::onTaskItemAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent(
    viewState: HomeViewState,
    searchKey: String,
    currency: String,
    startedTaskId: Int,
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
                                painter = painterResource(R.drawable.ic_logout), // Use ic_logout if available
                                contentDescription = "Logout",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
        ) { padding ->
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        onTasksScreenAction(HomeScreenActions.RefreshTasks)
                        // Simulate refresh delay - adjust based on your actual refresh logic
                        delay(1000)
                        isRefreshing = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                TrackingToggleButton(
                    isSignedIn = viewState.isTracking,
                    onToggle = { onTasksScreenAction(HomeScreenActions.ToggleTracking) }
                )

//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Gray100)
//                        .padding(
//                            top = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
//                            start = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
//                            end = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
//                        )
//                ) {
//                    SearchWithFilterBar(
//                        searchKey = searchKey,
//                        onSearchKeyChange = {
//                            onTasksScreenAction(
//                                HomeScreenActions.OnSearchKeyChange(it)
//                            )
//                        },
//                        onSearchTriggered = { onTasksScreenAction(HomeScreenActions.OnSearchTriggered) },
//                    )
//
//                    Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
//
//                    Tasks(viewState, currency, startedTaskId, onTasksScreenAction, onTaskItemAction)
//                }
            }
        }

        if (viewState.isLoading && !isRefreshing) {
            ShowLoadingIndicator(Modifier)
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

@Composable
fun LogoutDialog(
    isVisible: Boolean? = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible == true) {
        ConfirmDialog(
            title = stringResource(R.string.logout),
            message = stringResource(R.string.logout_message),
            onPrimaryClick = {
                onConfirm()
            },
            onDismiss = {
                onDismiss()
            }
        )
    }
}

@Composable
fun TrackingToggleButton(
    isSignedIn: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        androidx.compose.material3.FloatingActionButton(
            onClick = onToggle,
            containerColor = if (isSignedIn) Color(0xFF2E7D32) else Color(0xFFB0BEC5),
            contentColor = Color.White,
            modifier = Modifier.size(120.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isSignedIn) R.drawable.ic_start_tracking else R.drawable.ic_stop_tracking
                ),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isSignedIn) stringResource(R.string.work_started) else  stringResource(R.string.start_work),
            style = mdMedium.copy(color = Gray800)
        )


    }
}

@Composable
fun SearchWithFilterBar(
    searchKey: String,
    onSearchKeyChange: (String) -> Unit,
    onSearchTriggered: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    ) {
        SearchBox(
            modifier = Modifier
                .weight(1f)
                .testTag("searchBox"),
            searchKey,
            hint = stringResource(R.string.find_order),
            onSearchKeyChange = onSearchKeyChange,
            onSearchTriggered = onSearchTriggered,
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
fun Tasks(
    viewState: HomeViewState,
    currency: String,
    startedTaskId: Int,
    onTasksScreenAction: (HomeScreenActions) -> Unit,
    onTaskItemAction: (HomeItemActions) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                Log.d("TAG", "Tasks: On Scroll")
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex >= totalItems - 1) {
                    Log.d("TAG", "Tasks: Load More Data")
                    onTasksScreenAction(HomeScreenActions.LoadTasks)
                }
            }
    }

    val tasks = viewState.filteredTasks
    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimensionResource(com.intuit.sdp.R.dimen._90sdp))
                .testTag("emptyState"),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(R.string.no_tasks_available),
                style = mdRegular.copy(color = Gray500)
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.testTag("tasksList")
        ) {
            itemsIndexed(tasks) { _, task ->
                TaskCard(
                    homeTask = task,
                    currency = currency,
                    startedTaskId = startedTaskId,
                    onTaskDetailsClick = { taskId, retailerId ->
                        onTaskItemAction(
                            HomeItemActions.ShowTaskDetails(taskId, retailerId)
                        )
                    },
                    onDirectionClick = { lat, long ->
                        onTaskItemAction(
                            HomeItemActions.OpenDirection(lat, long)
                        )
                    },
                    onContactClick = {
                        onTaskItemAction(
                            HomeItemActions.OpenContactBottomSheet(task)
                        )
                    },
                    onClick = {
                        onTaskItemAction(HomeItemActions.TaskClicked(task))
                    })
            }
        }
    }
}

@Composable
fun TaskCard(
    homeTask: HomeTask,
    currency: String,
    startedTaskId: Int,
    onTaskDetailsClick: (taskId: Int, retailerId: Int) -> Unit,
    onDirectionClick: (Float, Float) -> Unit,
    onContactClick: (String) -> Unit,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        colors = CardDefaults.cardColors(containerColor = Base_white),
        border = BorderStroke(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            .testTag("taskCard_${homeTask.taskId}")
    ) {
        Column(
            modifier = Modifier.padding(
                all = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoRow(
                    icon = R.drawable.ic_hash,
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

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            InfoRow(
                icon = R.drawable.ic_profile,
                label = homeTask.clientPhone ?: stringResource(R.string.no_address),
                modifier = Modifier.testTag("clientName")
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            InfoRow(
                icon = R.drawable.ic_address,
                label = homeTask.customerAddress?.ifEmpty { stringResource(R.string.no_address) }
                    ?: stringResource(R.string.no_address),
                modifier = Modifier.testTag("clientAddress")
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            InfoRow(
                icon = R.drawable.ic_count,
                label = stringResource(R.string.products_count, homeTask.itemsCount.toString()),
                modifier = Modifier.testTag("productCount")
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            InfoRow(
                icon = R.drawable.ic_price,
                label = String.format(
                    stringResource(R.string.price_currency),
                    homeTask.totalPrice.toString(),
                    currency
                ),
                textStyle = mdMedium.copy(color = Gray800),
                modifier = Modifier.testTag("totalPrice")
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = Gray200
            )

            NavigationAction(
                clickedTaskId = homeTask.taskId,
                startedTaskId = startedTaskId,
                onTaskDetailsClick = { onTaskDetailsClick(homeTask.taskId, homeTask.taskId) },
                onDirectionClick = {
                    onDirectionClick(
                        homeTask.clientLang, homeTask.clientLat
                    )
                },
                onContactClick = { onContactClick(homeTask.clientPhone ?: "") }
            )
        }
    }
}

@Composable
fun InfoRow(
    icon: Int,
    label: String,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = smNormal.copy(color = Gray500),
    iconSize: Int = com.intuit.sdp.R.dimen._20sdp // uniform size for all icons
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(iconSize)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Gray500,
                modifier = Modifier.size(dimensionResource(iconSize))
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
        Text(
            text = label,
            style = textStyle
        )
    }
}

@Composable
fun StatusTag(
    statusId: Int,
    statusName: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (statusId) {
        TaskStatus.Cancelled.id -> Pair(Color(0xFFFFF4E6), Color(0xFFFF9800))
        TaskStatus.Completed.id -> Pair(Color(0xFFE8F5E9), Color(0xFF4CAF50))
        TaskStatus.InProgress.id -> Pair(Color(0xFFE3F2FD), Color(0xFF2196F3))
        TaskStatus.Pending.id -> Pair(Color(0xFFFFEBEE), Color(0xFFF44336))
        else -> Pair(Color(0xFFFFEBEE), Color(0xFFF44336))
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            )
            .padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._4sdp)
            )
    ) {
        Text(
            text = statusName,
            style = xsMedium.copy(color = textColor)
        )
    }
}

private fun fakeHomeViewState(): HomeViewState {
    return HomeViewState(
        isLoading = false,
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
        onTasksScreenAction = {},
        onTaskItemAction = {}
    )
}