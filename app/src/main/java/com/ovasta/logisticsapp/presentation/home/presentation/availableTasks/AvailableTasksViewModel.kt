package com.ovasta.logisticsapp.presentation.home.presentation.availableTasks

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AvailableTasksViewModel(
    private val homeRepository: IHomeRepository
) : BaseViewModel() {

    private val _tasks = MutableStateFlow<List<DeliveryTask>>(emptyList())
    val tasks = _tasks.asStateFlow()

    init {
        loadWaitingTasks()
    }

    private fun loadWaitingTasks() {
        viewModelScope.launch {
            setComposeUILoading(true)
            try {
                homeRepository.listenToNewDeliveryTasks(userId = 234, districtId = 1)
                    .collect { tasks ->
                        setComposeUILoading(false)
                        _tasks.value = tasks
                    }
            } catch (ex: Exception) {
                if (ex is kotlinx.coroutines.CancellationException) throw ex
                setComposeUILoading(false)
                emitComposeUIExceptionEvent(ex.toComposeUIException())
            }
        }
    }

    fun acceptOrder(orderId: Int) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.changeOrderStatus(orderId, OrderSteps.Assigned)
            }.onSuccess {
                setComposeUILoading(false)
                _tasks.update { list -> list.filter { it.orderId != orderId } }
            }.onFailure {
                setComposeUILoading(false)
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }
}
