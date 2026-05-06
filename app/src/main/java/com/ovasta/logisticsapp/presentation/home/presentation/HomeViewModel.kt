package com.ovasta.logisticsapp.presentation.home.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import com.ovasta.logisticsapp.base.ScreenDirection
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.nav.Home
import com.ovasta.logisticsapp.presentation.nav.Login
import com.ovasta.logisticsapp.presentation.nav.TaskDetails
import kotlinx.coroutines.Job

class HomeViewModel(
    private val context: Context,
    val homeRepository: IHomeRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState = _viewState.asStateFlow()

    private var assignedTasksJob: Job? = null
    private var sellersTasksJob: Job? = null

    fun getSellersTasks() {
        sellersTasksJob?.cancel()
        sellersTasksJob = viewModelScope.launch {
            setComposeUILoading(true)
            try {
                homeRepository.getAvailableSellerOrders(userId = 1, districtId = 1)
                    .collect { tasks ->
                        setComposeUILoading(false)
                        _viewState.update { it.copy(sellerTasks = tasks) }
                    }
            } catch (ex: Exception) {
                if (ex is kotlinx.coroutines.CancellationException) throw ex
                setComposeUILoading(false)
                updateViewStateWithFail(ex)
            }
        }
    }

    fun getAssignedTasks() {
        assignedTasksJob?.cancel()
        assignedTasksJob = viewModelScope.launch {
            setComposeUILoading(true)
            try {
                homeRepository.getAssignedTasks(userId = 1, districtId = 1).collect { tasks ->
                    setComposeUILoading(false)
                    _viewState.update { it.copy(tasks = tasks, filteredTasks = tasks) }
                }
            } catch (ex: Exception) {
                if (ex is kotlinx.coroutines.CancellationException) throw ex
                setComposeUILoading(false)
                updateViewStateWithFail(ex)
            }
        }
    }

    private val _searchKey = MutableStateFlow<String?>(null)
    var searchKey: StateFlow<String?> = _searchKey

    private val _currency = MutableStateFlow("")
    val currency: StateFlow<String> = _currency


    private val _taskItemActions = MutableSharedFlow<HomeItemActions?>()
    val taskItemActions = _taskItemActions.asSharedFlow()

    private val _showContactSheet = MutableStateFlow<HomeTask?>(null)
    val showContactSheet: StateFlow<HomeTask?> = _showContactSheet


    var startedTaskId = 0

    init {
        observeSearchKey()
        getPartnerStatus()
        getPartnerStatistics()
    }

    fun onTasksScreenAction(tasksScreenAction: HomeScreenActions) {
        when (tasksScreenAction) {
            is HomeScreenActions.OnSearchKeyChange -> {
                _searchKey.value = tasksScreenAction.searchKey
            }

            is HomeScreenActions.ClearToastMessage -> clearToastMessage()
            HomeScreenActions.LoadTasks -> {}
            HomeScreenActions.OnSearchTriggered -> {}
            HomeScreenActions.RefreshTasks -> {
                getPartnerStatistics()
                getPartnerStatus()
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
                        monthFilter = tasksScreenAction.month,
                        yearFilter = tasksScreenAction.year
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

    fun taskClicked(homeTask: HomeTask) {
    }

    private fun observeSearchKey() {

    }

    fun updateSearchKey(query: String) {
        _searchKey.value = query
    }

    fun searchTasks() {
        val query = _searchKey.value
        if (query.isNullOrBlank()) {
            updateUiState(_viewState.value.copy(filteredTasks = _viewState.value.tasks))
            return
        } else {
            viewState.value.tasks.filter {
                it.customerName?.contains(
                    query, ignoreCase = true
                ) == true || it.taskId.toString()
                    .contains(query, ignoreCase = true) || it.clientPhone?.contains(
                    query, ignoreCase = true
                ) == true
            }.let { filteredTasks ->
                updateUiState(_viewState.value.copy(filteredTasks = filteredTasks))
            }
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

//                updateUiState(
//                    viewState.value.copy(
//                        partnerStatistics = PartnerStatistics(
//                            2300.0,
//                            1500.0,
//                            7,
//                            50,
//                            "2024-12-31"
//                        )
//                    )
//                )
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
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    fun updateUiState(homeViewState: HomeViewState) {
        _viewState.value = homeViewState
    }

    private fun clearToastMessage() {
        _viewState.update { it.copy(showToastMessage = null) }
    }
}