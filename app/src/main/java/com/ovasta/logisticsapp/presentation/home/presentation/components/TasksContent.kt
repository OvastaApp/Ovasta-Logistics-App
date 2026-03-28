package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.components.sharedComposable.ToastMsg
import com.ovasta.logisticsapp.base.mdRegular
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.presentation.HomeItemActions
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreenActions
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent(
    viewState: HomeViewState,
    searchKey: String,
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
                                painter = painterResource(R.drawable.ic_logout), // Use ic_logout if available
                                contentDescription = "Logout",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
        ) { padding ->
            val listState = rememberLazyListState()
            val tasks = viewState.filteredTasks

            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collect { lastVisibleItemIndex ->
                        val totalItems = listState.layoutInfo.totalItemsCount
                        if (lastVisibleItemIndex >= totalItems - 1) {
                            onTasksScreenAction(HomeScreenActions.LoadTasks)
                        }
                    }
            }

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
                        .padding(
                            start = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                            end = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                        )
                        .testTag("tasksList"),
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
                    if (partnerStatistics != null) {
                        item(key = "statistics") {
                            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                            PartnerStatisticsSection(
                                statistics = partnerStatistics
                            )
                        }
                    }

                    // Search bar
                    item(key = "search") {
                        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                        SearchWithFilterBar(
                            searchKey = searchKey,
                            onSearchKeyChange = {
                                onTasksScreenAction(
                                    HomeScreenActions.OnSearchKeyChange(it)
                                )
                            },
                            onSearchTriggered = { onTasksScreenAction(HomeScreenActions.OnSearchTriggered) },
                        )
                    }

                    // Tasks list
                    if (tasks.isEmpty()) {
                        item(key = "empty") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dimensionResource(com.intuit.sdp.R.dimen._90sdp))
                                    .testTag("emptyState"),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = stringResource(R.string.no_tasks_available),
                                    style = mdRegular.copy(color = Gray500)
                                )
                            }
                        }
                    } else {

                        itemsIndexed(tasks, key = { _, task -> task.taskId }) { _, task ->
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
                            onContactClick = { phone ->
                                onTaskItemAction(
                                    HomeItemActions.CallRetailer(phone)
                                )
                            },
                            onWhatsAppClick = { phone ->
                                onTaskItemAction(
                                    HomeItemActions.WhatsAppRetailer(phone)
                                )
                            },
                            onClick = {
                                onTaskItemAction(HomeItemActions.TaskClicked(task))
                            }
                        )
                        }
                    }

                    // Bottom spacing
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

@Preview
@Composable
fun TasksContentPreview() {
        TasksContent(
            viewState = HomeViewState(
                filteredTasks = listOf(),
                isTracking = false,
                showToastMessage = null
            ),
            searchKey = "",
            currency = "$",
            startedTaskId = -1,
            partnerStatistics = PartnerStatistics(
            12.3, 450.0, 67, 89
            ),
            onTasksScreenAction = {},
            onTaskItemAction = {}
        )
}