package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.orderDetails.data.IOrderDetailsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailsViewModel(
    private val orderDetailsRepository: IOrderDetailsRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {
    private val _viewState = MutableStateFlow(TaskDetailsViewState())
    val viewState = _viewState.asStateFlow()

    private var assignedTasksJob: Job? = null


    fun getTaskDetails(taskId: Int) {
        assignedTasksJob?.cancel()
        assignedTasksJob = viewModelScope.launch {
            setComposeUILoading(true)
            try {
                orderDetailsRepository.listenToOrderChanges(
                    districtId = settingsRepository.getUseData()?.districtId ?: 0,
                    taskId = taskId
                ).collect { task ->
                    setComposeUILoading(false)
                    _viewState.update { it.copy(task = task) }
                }
            } catch (ex: Exception) {
                if (ex is kotlinx.coroutines.CancellationException) throw ex
                setComposeUILoading(false)
                updateViewStateWithFail(ex)
            }
        }
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}