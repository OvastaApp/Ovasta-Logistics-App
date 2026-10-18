package com.ovasta.logisticsapp.presentation.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.components.sharedComposable.Navigator
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginScreen
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginViewModel
import com.ovasta.logisticsapp.presentation.auth.splash.SplashScreen
import com.ovasta.logisticsapp.presentation.auth.splash.SplashViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.HomeItemActions
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreen
import com.ovasta.logisticsapp.presentation.home.presentation.HomeViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.ScreenFlashOverlay
import com.ovasta.logisticsapp.presentation.home.presentation.availableTasks.AvailableTasksScreen
import com.ovasta.logisticsapp.presentation.home.presentation.availableTasks.AvailableTasksViewModel
import com.ovasta.logisticsapp.presentation.home.presentation.components.NewDeliveryTaskBottomSheet
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.DropOfOrderDetailsScreen
import com.ovasta.logisticsapp.presentation.orderDetails.presentation.TaskDetailsViewModel
import org.koin.androidx.compose.koinViewModel

data object Splash
data object Login
data object Home
data object AvailableTasks
data class TaskDetails(val taskId: Int)


@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Any>(Splash) }
    val navigator = remember { Navigator(backStack) }
    
    // HomeViewModel at nav-level — alive once Home is reached (not on Splash/Login).
    // It and the other logged-in screens' ViewModels live in the session store, not the
    // Activity's, so logging out destroys them.
    val sessionStoreOwner: SessionViewModelStoreOwner = viewModel()
    val isLoggedIn = backStack.any { it is Home }
    val homeViewModel: HomeViewModel? = if (isLoggedIn) {
        koinViewModel<HomeViewModel>(viewModelStoreOwner = sessionStoreOwner)
    } else null

    // On logout (Home leaves the back stack) clear the session, so the next account gets a fresh
    // HomeViewModel instead of the previous one's name, Firebase listeners and tracking state
    var wasLoggedIn by remember { mutableStateOf(false) }
    LaunchedEffect(isLoggedIn) {
        if (wasLoggedIn && !isLoggedIn) sessionStoreOwner.clearSession()
        wasLoggedIn = isLoggedIn
    }

    // Observe app foreground/background to pause/resume the alert countdown timer
    if (homeViewModel != null) {
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            homeViewModel.onAppForeground()
        }
        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
            homeViewModel.onAppBackground()
        }
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        Scaffold(bottomBar = {}) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                NavDisplay(
                    modifier = modifier,
                    backStack = backStack,
                    onBack = { navigator.pop() },

                    entryProvider = { key ->
                        when (key) {
                            is Splash -> NavEntry(key) {
                                val viewModel: SplashViewModel = koinViewModel()
                                SplashScreen(viewModel)
                            }

                            is Login -> NavEntry(key) {
                                val viewModel: LoginViewModel = koinViewModel()
                                LoginScreen(viewModel)
                            }

                            is Home -> NavEntry(key) {
                                // Use the hoisted HomeViewModel instead of creating a new one
                                HomeScreen(homeViewModel!!)
                            }

                            is AvailableTasks -> NavEntry(key) {
                                val viewModel: AvailableTasksViewModel =
                                    koinViewModel(viewModelStoreOwner = sessionStoreOwner)
                                AvailableTasksScreen(
                                    viewModel = viewModel,
                                    onOrderAccepted = {
                                        homeViewModel?.getAssignedDeliveryOrders()
                                    }
                                )
                            }

                            is TaskDetails -> NavEntry(key) {
                                val viewModel: TaskDetailsViewModel =
                                    koinViewModel(viewModelStoreOwner = sessionStoreOwner)
                                DropOfOrderDetailsScreen(
                                    viewModel = viewModel,
                                    taskId = key.taskId,
                                )
                            }

                            else -> NavEntry(Unit) { Text("Unknown route") }
                        }
                    }
                )
                
                // Global bottom sheet overlay — shows on ANY screen after login
                if (homeViewModel != null) {
                    val viewState by homeViewModel.viewState.collectAsState()
                    val currency by homeViewModel.currency.collectAsState()
                    val currentTask = viewState.currentAlertTask
                    
                    if (currentTask != null && !viewState.bottomSheetMinimized && viewState.isTracking) {
                        ScreenFlashOverlay()
                        NewDeliveryTaskBottomSheet(
                            task = currentTask,
                            currency = currency,
                            taskAlertTimestamps = homeViewModel.taskAlertTimestamps,
                            onAccept = { orderId ->
                                homeViewModel.onTaskItemAction(HomeItemActions.AcceptDeliveryTask(orderId))
                            },
                            onMinimize = {
                                homeViewModel.onTaskItemAction(HomeItemActions.MinimizeBottomSheet)
                            }
                        )
                    }
                }
            }
        }
    }
}
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewHomeNavigationBar() {
    AppNavHost()
}