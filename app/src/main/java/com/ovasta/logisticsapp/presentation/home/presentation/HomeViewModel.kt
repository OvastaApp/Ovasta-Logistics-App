package com.ovasta.logisticsapp.presentation.home.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.ext.OrderAlarmSound
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import com.ovasta.logisticsapp.base.ScreenDirection
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import com.ovasta.logisticsapp.presentation.nav.Login
import com.ovasta.logisticsapp.presentation.nav.TaskDetails
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

class HomeViewModel(
    private val context: Context,
    val homeRepository: IHomeRepository,
    val settingsRepository: ISettingsRepository,
    private val orderAlarmSound: OrderAlarmSound
) : BaseViewModel() {
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState = _viewState.asStateFlow()

    private var assignedTasksJob: Job? = null
    private var sellersTasksJob: Job? = null
    private var alertTimerJob: Job? = null
    private val _taskAlertTimestamps = mutableMapOf<Int, Long>()
    val taskAlertTimestamps: Map<Int, Long> get() = _taskAlertTimestamps
    
    // Persist seen task IDs across app restarts
    private val prefs = context.getSharedPreferences("delivery_alerts", Context.MODE_PRIVATE)
    private val _seenTaskIds = prefs.getStringSet("seen_task_ids", emptySet())!!
        .mapTo(mutableSetOf()) { it.toInt() }


    private val _currency = MutableStateFlow("")
    val currency: StateFlow<String> = _currency


    private val _taskItemActions = MutableSharedFlow<HomeItemActions?>()
    val taskItemActions = _taskItemActions.asSharedFlow()

    private val _showContactSheet = MutableStateFlow<HomeTask?>(null)
    val showContactSheet: StateFlow<HomeTask?> = _showContactSheet


    var startedTaskId = 0

    fun listenToNewDeliveryTasks() {
        sellersTasksJob?.cancel()
        sellersTasksJob = viewModelScope.launch {
            setComposeUILoading(true)
            delay(3000)
            try {
                homeRepository.listenToNewDeliveryTasks(userId = 234, districtId = 1)
                    .collect { tasks ->
                        setComposeUILoading(false)
                        Log.d("listenToNewDeliveryTasks", "Received new delivery tasks: $tasks")
                        
                        // Cleanup stale IDs (tasks no longer in Firebase)
                        val currentTaskIds = tasks.map { it.orderId }.toSet()
                        _seenTaskIds.removeAll { it !in currentTaskIds }
                        persistSeenTaskIds()
                        
                        // Sync queue/current with Firebase reality (remove tasks that disappeared from Firebase)
                        _viewState.update { state ->
                            val syncedQueue = state.alertQueue.filter { it.orderId in currentTaskIds }
                            val syncedCurrent = if (state.currentAlertTask?.orderId in currentTaskIds) {
                                state.currentAlertTask
                            } else {
                                null
                            }
                            state.copy(
                                waitingDeliveryTasks = tasks,
                                alertQueue = syncedQueue,
                                currentAlertTask = syncedCurrent
                            )
                        }

                        // Filter out tasks that are already handled or currently in pipeline
                        val alreadyInPipeline = buildSet {
                            _viewState.value.currentAlertTask?.orderId?.let { add(it) }
                            addAll(_viewState.value.alertQueue.map { it.orderId })
                        }
                        val newTasks = tasks.filter { 
                            it.orderId !in _seenTaskIds && it.orderId !in alreadyInPipeline 
                        }

                        // Queue new tasks only if tracking is enabled
                        if (newTasks.isNotEmpty() && _viewState.value.isTracking) {
                            _viewState.update { state ->
                                if (state.currentAlertTask == null) {
                                    // No task showing → show first one immediately
                                    val firstTask = newTasks.first()
                                    _taskAlertTimestamps[firstTask.orderId] = System.currentTimeMillis()
                                    state.copy(
                                        currentAlertTask = firstTask,
                                        alertQueue = state.alertQueue + newTasks.drop(1),
                                        bottomSheetMinimized = false
                                    )
                                } else {
                                    // Task already showing → queue all new tasks
                                    state.copy(
                                        alertQueue = state.alertQueue + newTasks,
                                        bottomSheetMinimized = false
                                    )
                                }
                            }
                            // Start alarm sound when new tasks arrive
                            orderAlarmSound.startAlarm()
                        }
                    }
            } catch (ex: Exception) {
                if (ex is kotlinx.coroutines.CancellationException) throw ex
                setComposeUILoading(false)
                updateViewStateWithFail(ex)
            }
        }
    }

    fun getAssignedOrders() {
        assignedTasksJob?.cancel()
        assignedTasksJob = viewModelScope.launch {
            setComposeUILoading(true)
            try {
                homeRepository.getAssignedOrders(userId = 234, districtId = 1).collect { tasks ->
                    setComposeUILoading(false)
                    _viewState.update { it.copy(appTasks = tasks) }
                }
            } catch (ex: Exception) {
                if (ex is kotlinx.coroutines.CancellationException) throw ex
                setComposeUILoading(false)
                updateViewStateWithFail(ex)
            }
        }
    }

    fun getAssignedDeliveryOrders() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.getAssignedDeliveryOrders()
            }.onSuccess {
                setComposeUILoading(false)
                _viewState.update { state -> state.copy(assignedDeliveryTasks = it) }
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    init {
        getPartnerStatus()
        getPartnerStatistics()
        getAssignedOrders()
        listenToNewDeliveryTasks()
        getAssignedDeliveryOrders()
        startAlertExpiryTimer()
    }

    private fun startAlertExpiryTimer() {
        alertTimerJob?.cancel()
        alertTimerJob = viewModelScope.launch {
            while (true) {
                delay(500)
                val currentTask = _viewState.value.currentAlertTask
                if (currentTask != null) {
                    val timestamp = _taskAlertTimestamps[currentTask.orderId]
                    if (timestamp != null && System.currentTimeMillis() - timestamp >= 30_000) {
                        // Current task expired → mark as seen and pop next from queue
                        _seenTaskIds.add(currentTask.orderId)
                        persistSeenTaskIds()
                        _taskAlertTimestamps.remove(currentTask.orderId)
                        popNextTaskFromQueue()
                    }
                }
            }
        }
    }

    private fun popNextTaskFromQueue() {
        _viewState.update { state ->
            val nextTask = state.alertQueue.firstOrNull()
            if (nextTask != null) {
                // Pop from queue, set as current with fresh timestamp
                _taskAlertTimestamps[nextTask.orderId] = System.currentTimeMillis()
                state.copy(
                    currentAlertTask = nextTask,
                    alertQueue = state.alertQueue.drop(1),
                    bottomSheetMinimized = false
                )
            } else {
                // Queue empty → clear current task, stop alarm
                orderAlarmSound.stopAlarm()
                state.copy(
                    currentAlertTask = null,
                    bottomSheetMinimized = false
                )
            }
        }
    }

    private fun persistSeenTaskIds() {
        prefs.edit().putStringSet("seen_task_ids", _seenTaskIds.map { it.toString() }.toSet()).apply()
    }

    fun onTasksScreenAction(tasksScreenAction: HomeScreenActions) {
        when (tasksScreenAction) {
            is HomeScreenActions.ClearToastMessage -> clearToastMessage()
            HomeScreenActions.LoadTasks -> {}
            HomeScreenActions.RefreshTasks -> {
                getPartnerStatistics()
                getPartnerStatus()
                listenToNewDeliveryTasks()
            }

            HomeScreenActions.ToggleTracking -> {
                changePartnerStatus(!viewState.value.isTracking)
            }

            is HomeScreenActions.ChangeLogoutDialogStatus -> {
                updateUiState(viewState.value.copy(isLogoutDialogVisible = tasksScreenAction.isVisible))
            }

            HomeScreenActions.OnLogoutClicked -> {
                logout()
            }

            is HomeScreenActions.OnMonthYearFilterChanged -> {
                _viewState.update {
                    it.copy(
                        monthFilter = tasksScreenAction.month, yearFilter = tasksScreenAction.year
                    )
                }
                getPartnerStatistics()
            }
        }
    }


    fun onTaskItemAction(taskItemAction: HomeItemActions) {
        when (taskItemAction) {
            is HomeItemActions.OpenContactBottomSheet -> {
                if (taskItemAction.homeTask.clientPhone.isNullOrBlank()) {
                    updateUiState(viewState.value.copy(showToastMessage = R.string.phone_number_not_available))
                } else {
                    _showContactSheet.value = taskItemAction.homeTask
                }
            }

            is HomeItemActions.OpenDirection -> {
                if (taskItemAction.lat == 0.0 || taskItemAction.lng == 0.0) {
                    updateUiState(_viewState.value.copy(showToastMessage = R.string.location_not_available))
                } else {
                    viewModelScope.launch {
                        _taskItemActions.emit(taskItemAction)
                    }
                }
            }

            is HomeItemActions.CallRetailer -> {
                if (taskItemAction.clientPhone.isBlank()) {
                    updateUiState(viewState.value.copy(showToastMessage = R.string.phone_number_not_available))
                } else {
                    viewModelScope.launch {
                        _taskItemActions.emit(taskItemAction)
                    }
                }
            }

            is HomeItemActions.WhatsAppRetailer -> {
                if (taskItemAction.clientWhatsapp.isBlank()) {
                    updateUiState(viewState.value.copy(showToastMessage = R.string.phone_number_not_available))
                } else {
                    viewModelScope.launch {
                        _taskItemActions.emit(taskItemAction)
                    }
                }
            }

            is HomeItemActions.TaskClicked -> {
                emitScreenDirectionEvent(ScreenDirection.Push(TaskDetails(taskId = taskItemAction.taskId)))
            }

            is HomeItemActions.DismissContactBottomSheet -> {
                _showContactSheet.value = null
            }

            is HomeItemActions.AcceptDeliveryTask -> {
                acceptDeliveryOrder(taskItemAction.orderId)
            }

            is HomeItemActions.MinimizeBottomSheet -> {
                // Mark only current task as seen (not queued tasks)
                _viewState.value.currentAlertTask?.orderId?.let { orderId ->
                    _seenTaskIds.add(orderId)
                    persistSeenTaskIds()
                    _taskAlertTimestamps.remove(orderId)
                }
                // Clear current task (it's been marked as seen) — keep queue intact
                _viewState.update { it.copy(currentAlertTask = null, bottomSheetMinimized = true) }
                orderAlarmSound.stopAlarm()
            }

            else -> {
                viewModelScope.launch {
                    _taskItemActions.emit(taskItemAction)
                }
            }
        }
    }

    private fun toggleTracking(shouldBeTracking: Boolean) {
        viewModelScope.launch {
            if (shouldBeTracking) {
                startTracking()
            } else {
                stopTracking()
            }
        }
    }

    private fun startTracking() {
        viewModelScope.launch {
            try {
                homeRepository.startLocationTracking(context)
                updateUiState(_viewState.value.copy(isTracking = true))

                // If there are waiting tasks not yet seen, queue them
                val waitingTasks = _viewState.value.waitingDeliveryTasks
                val unseenTasks = waitingTasks.filter { it.orderId !in _seenTaskIds }
                
                if (unseenTasks.isNotEmpty()) {
                    _viewState.update { state ->
                        if (state.currentAlertTask == null) {
                            // Show first task immediately
                            val firstTask = unseenTasks.first()
                            _taskAlertTimestamps[firstTask.orderId] = System.currentTimeMillis()
                            state.copy(
                                currentAlertTask = firstTask,
                                alertQueue = state.alertQueue + unseenTasks.drop(1),
                                bottomSheetMinimized = false
                            )
                        } else {
                            // Queue all unseen tasks
                            state.copy(
                                alertQueue = state.alertQueue + unseenTasks,
                                bottomSheetMinimized = false
                            )
                        }
                    }
                    // Start alarm
                    orderAlarmSound.startAlarm()
                }
            } catch (ex: Exception) {
                updateUiState(_viewState.value.copy(isTracking = false))
                error.value = ex
                Log.e("HomeViewModel", "Failed to start tracking", ex)
            }
        }
    }

    private fun stopTracking() {
        viewModelScope.launch {
            try {
                homeRepository.stopLocationTracking(context)
                updateUiState(_viewState.value.copy(isTracking = false))
                // Stop alarm when tracking is disabled
                orderAlarmSound.stopAlarm()
                // Clear current task and queue
                _viewState.update { it.copy(currentAlertTask = null, alertQueue = emptyList()) }
            } catch (ex: Exception) {
                updateUiState(_viewState.value.copy(isTracking = true))
                error.value = ex
                Log.e("HomeViewModel", "Failed to stop tracking", ex)
            }
        }
    }

    fun clearTasksItemActions() {
        viewModelScope.launch {
            _taskItemActions.emit(null)
        }
    }


    fun logout() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                settingsRepository.logout()
            }.onSuccess {
                setComposeUILoading(false)
                stopTracking()
                settingsRepository.clearUserData()
                emitScreenDirectionEvent(ScreenDirection.Replace(Login))
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    private fun getPartnerStatus() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.getPartnerStatus()
            }.onSuccess { profileConfig ->
                setComposeUILoading(false)
                val isOnline = profileConfig.data.isOnline ?: false
                if (isOnline && !isLocationEnabled()) {
                    // Backend says online but location is off — don't start tracking, set offline
                    updateUiState(_viewState.value.copy(isTracking = false))
                    changePartnerStatus(false)
                } else {
                    updateUiState(_viewState.value.copy(isTracking = isOnline))
                    toggleTracking(shouldBeTracking = isOnline)
                }
            }.onFailure {
                setComposeUILoading(false)
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }

    private fun changePartnerStatus(isOnline: Boolean) {
        if (isOnline && !isLocationEnabled()) {
            updateUiState(_viewState.value.copy(showToastMessage = R.string.enable_location_to_go_online))
            return
        }
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.changePartnerStatus(isOnline = isOnline)
            }.onSuccess {
                setComposeUILoading(false)
                toggleTracking(shouldBeTracking = isOnline)
            }.onFailure {
                setComposeUILoading(false)
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }

    private fun getPartnerStatistics(
    ) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.getPartnerStatistics(
                    viewState.value.monthFilter, viewState.value.yearFilter
                )
            }.onSuccess { response ->
                setComposeUILoading(false)
                updateUiState(viewState.value.copy(partnerStatistics = response.data))
            }.onFailure {
                setComposeUILoading(false)
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }

    fun changeDeliveryOrderStatus(orderId: Int, status: OrderSteps) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.changeOrderStatus(orderId, status)
            }.onSuccess {
                setComposeUILoading(false)
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    private fun acceptDeliveryOrder(orderId: Int) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.changeOrderStatus(orderId, OrderSteps.Assigned)
            }.onSuccess {
                setComposeUILoading(false)
                // Mark as seen and persist
                _seenTaskIds.add(orderId)
                persistSeenTaskIds()
                
                // Remove timestamp
                _taskAlertTimestamps.remove(orderId)
                
                // Remove from waiting list
                _viewState.update { state ->
                    state.copy(
                        waitingDeliveryTasks = state.waitingDeliveryTasks.filter { it.orderId != orderId }
                    )
                }
                
                // Pop next task from queue
                popNextTaskFromQueue()
                
                // Refresh assigned orders to get fresh data
                getAssignedDeliveryOrders()
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }


    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            android.location.LocationManager.NETWORK_PROVIDER
        )
    }

    fun updateUiState(homeViewState: HomeViewState) {
        _viewState.value = homeViewState
    }

    private fun clearToastMessage() {
        _viewState.update { it.copy(showToastMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        orderAlarmSound.stopAlarm()
    }
}