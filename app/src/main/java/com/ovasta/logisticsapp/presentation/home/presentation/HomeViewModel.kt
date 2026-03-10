package com.ovasta.logisticsapp.presentation.home.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.data.User
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
import com.ovasta.logisticsapp.base.exception.APIException
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class HomeViewModel(
    private val context: Context,
    val homeRepository: IHomeRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState = _viewState.asStateFlow()

    fun getAssignedTasks() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            try {
                homeRepository.getAssignedTasks(1, 1, "pickers").collect { tasks ->
                    _viewState.update { it.copy(isLoading = false) }
                    Log.d("assignedTasksVM", "$tasks")

                    val previousTasks = _viewState.value.tasks

                    _viewState.update { it.copy(tasks = tasks, filteredTasks = tasks) }

                    // Vibrate only if there are new orders
                    // if (tasks.size > previousTasks.size) {
                    //     orderVibrator.vibrateNewOrder()
                    // }
                }
            } catch (ex: Exception) {
                _viewState.update { it.copy(isLoading = false) }
                error.value = ex
            }
        }
    }

    suspend fun getUserData(): User? {
        return settingsRepository.getUseData()
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
        observeTracking()
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
                getAssignedTasks()
            }

            HomeScreenActions.ToggleTracking -> {
                toggleTracking()
            }

            is HomeScreenActions.ChangeLogoutDialogStatus -> {
                _viewState.update { it.copy(isLogoutDialogVisible = tasksScreenAction.isVisible) }
            }

            HomeScreenActions.OnLogoutClicked -> {
                logout()
            }
        }
    }


    fun onTaskItemAction(taskItemAction: HomeItemActions) {
        when (taskItemAction) {
            is HomeItemActions.OpenContactBottomSheet -> {
                if (taskItemAction.homeTask.clientPhone.isNullOrBlank()) {
                    _viewState.update { it.copy(showToastMessage = R.string.phone_number_not_available) }
                } else {
                    _showContactSheet.value = taskItemAction.homeTask
                }
            }

            is HomeItemActions.OpenDirection -> {
                if (taskItemAction.lat == 0.0f || taskItemAction.lng == 0.0f) {
                    _viewState.update { it.copy(showToastMessage = R.string.location_not_available) }
                } else {
                    viewModelScope.launch {
                        _taskItemActions.emit(taskItemAction)
                    }
                }
            }

            is HomeItemActions.TaskClicked -> taskClicked(taskItemAction.homeTask)

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

    private fun toggleTracking() {
        viewModelScope.launch {
            val isTracking = homeRepository.observeShiftStatus().first()
            if (isTracking) {
                stopTracking()
            } else {
                startTracking()
            }
        }
    }

    private fun startTracking() {
        viewModelScope.launch {
            try {
                _viewState.update { it.copy(isLoading = true) }
                homeRepository.startLocationTracking(context)
                delay(1000)
                _viewState.update { it.copy(isTracking = true, isLoading = false) }
            } catch (ex: Exception) {
                _viewState.update { it.copy(isTracking = false, isLoading = false) }
                error.value = ex
                Log.e("HomeViewModel", "Failed to start tracking", ex)
            }
        }
    }

    private fun stopTracking() {
        viewModelScope.launch {
            try {
                _viewState.update { it.copy(isLoading = true) }
                homeRepository.stopLocationTracking(context)
                delay(1000)
                _viewState.update { it.copy(isTracking = false, isLoading = false) }
            } catch (ex: Exception) {
                _viewState.update { it.copy(isTracking = true, isLoading = false) }
                error.value = ex
                Log.e("HomeViewModel", "Failed to stop tracking", ex)
            }
        }
    }

    private fun observeTracking() {
        viewModelScope.launch {
            settingsRepository.observeShiftStatus().collect { isTracking ->
                _viewState.update {
                    it.copy(isTracking = isTracking)
                }
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
            _viewState.update { it.copy(filteredTasks = it.tasks) }
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
                _viewState.update { it.copy(filteredTasks = filteredTasks) }
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
                emitScreenDirectionEvent(LoginScreen)
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }

    private fun clearToastMessage() {
        _viewState.update { it.copy(showToastMessage = null) }
    }
}