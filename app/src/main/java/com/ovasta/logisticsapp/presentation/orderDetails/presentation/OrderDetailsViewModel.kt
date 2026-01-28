package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    val orderDetailsRepository: IOrderDetailsRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {
    private val _viewState = MutableStateFlow(OrderDetailsViewState())
    val viewState = _viewState.asStateFlow()

    fun getTaskDetails() {
        viewModelScope.launch {
            loadingOn()
            try {
//                with(getUserData()) {
//                    this?.let {
                orderDetailsRepository.getTaskDetails(
                    1, 1
                ).collect { tasks ->
                    loadingOff()
//                    Log.d("assignedTasksVM", "$tasks")
//                    _viewState.value = _viewState.value.copy(
//                        tasks = tasks,
//                        filteredTasks = tasks
//                    )
                }
//                    }
//                }
            } catch (ex: Exception) {
                loadingOff()
                error.value = ex
            }
        }
    }

    suspend fun getUserData(): User? {
        return settingsRepository.getUseData()
    }


    private val _currency = MutableStateFlow("")
    val currency: StateFlow<String> = _currency


    private val _taskItemActions = MutableSharedFlow<OrderDetailsItemActions?>()
    val taskItemActions = _taskItemActions.asSharedFlow()

    private val _showContactSheet = MutableStateFlow<HomeTask?>(null)
    val showContactSheet: StateFlow<HomeTask?> = _showContactSheet


    var startedTaskId = 0

    init {
        observeSearchKey()
    }

    fun onTasksScreenAction(tasksScreenAction: OrderDetailsAction) {
        when (tasksScreenAction) {
            is OrderDetailsAction.OnSearchKeyChange -> {
//                _searchKey.value = tasksScreenAction.searchKey
            }

            is OrderDetailsAction.ClearToastMessage -> clearToastMessage()
            OrderDetailsAction.LoadTasks -> {}
            OrderDetailsAction.OnSearchTriggered -> {}
        }
    }


    fun onTaskItemAction(taskItemAction: OrderDetailsItemActions) {
        when (taskItemAction) {
            is OrderDetailsItemActions.OpenContactBottomSheet -> {
                if (taskItemAction.homeTask.clientPhone.isNullOrBlank()) {
                    _viewState.update { it.copy(showToastMessage = R.string.phone_number_not_available) }
                } else {
                    _showContactSheet.value = taskItemAction.homeTask
                }
            }

            is OrderDetailsItemActions.OpenDirection -> {
                if (taskItemAction.lat == 0.0f || taskItemAction.lng == 0.0f) {
                    _viewState.update { it.copy(showToastMessage = R.string.location_not_available) }
                } else {
                    viewModelScope.launch {
                        _taskItemActions.emit(taskItemAction)
                    }
                }
            }

            is OrderDetailsItemActions.TaskClicked -> taskClicked(taskItemAction.homeTask)

            is OrderDetailsItemActions.DismissContactBottomSheet -> {
                _showContactSheet.value = null
            }

            else -> {
                viewModelScope.launch {
                    _taskItemActions.emit(taskItemAction)
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




    private fun clearToastMessage() {
        _viewState.update { it.copy(showToastMessage = null) }
    }

}