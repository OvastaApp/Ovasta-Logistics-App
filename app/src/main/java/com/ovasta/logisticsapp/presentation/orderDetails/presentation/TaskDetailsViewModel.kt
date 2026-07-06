package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.ext.ToastEvent
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
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
    private var taskId: Int = 0

    fun getTaskDetails(taskId: Int) {
        this.taskId = taskId

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

    /**
     * Persists an edit to a single product's price and/or quantity by rewriting the order's
     * `products` array in Firestore. The snapshot listener started in [getTaskDetails] reflects
     * the change back into [viewState] automatically.
     *
     * @param index position of the product within the current product list.
     * @param newPrice the new unit price; when null the existing price is kept.
     * @param newQuantity the new quantity; when null the existing quantity is kept.
     */
    fun updateProduct(index: Int, newPrice: Int?, newQuantity: Int?) {
        viewModelScope.launch {
            val products = _viewState.value.task.products.toMutableList()
            val product = products.getOrNull(index) ?: return@launch

            val price = newPrice ?: product.itemPrice ?: 0
            val quantity = newQuantity ?: product.quantity ?: 0

            products[index] = product.copy(
                itemPrice = price,
                quantity = quantity,
                totalPrice = price.toDouble() * quantity
            )

            setComposeUILoading(true)
            kotlin.runCatching {
                orderDetailsRepository.updateProducts(
                    districtId = settingsRepository.getUseData()?.districtId ?: 0,
                    taskId = taskId,
                    products = products
                )
            }.onSuccess {
                setComposeUILoading(false)
                emitToastEvent(ToastEvent.ResourceToastEvent(R.string.product_updated_successfully))
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    /**
     * Removes a single product from the order by rewriting the order's `products` array in
     * Firestore without the product at [index]. The snapshot listener started in [getTaskDetails]
     * reflects the removal back into [viewState] automatically.
     *
     * @param index position of the product to delete within the current product list.
     */
    fun deleteProduct(index: Int) {
        viewModelScope.launch {
            val products = _viewState.value.task.products.toMutableList()
            if (index !in products.indices) return@launch
            products.removeAt(index)

            setComposeUILoading(true)
            kotlin.runCatching {
                orderDetailsRepository.updateProducts(
                    districtId = settingsRepository.getUseData()?.districtId ?: 0,
                    taskId = taskId,
                    products = products
                )
            }.onSuccess {
                setComposeUILoading(false)
                emitToastEvent(ToastEvent.ResourceToastEvent(R.string.product_deleted_successfully))
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    /**
     * Marks a product as picked with the given found quantity. Display-only for now — the value
     * is kept in [viewState] so the required-vs-found UI updates immediately. Persisting to
     * Firestore will be wired once the backend API is available; the snapshot listener will then
     * reflect the value via [com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct.pickedQuantity].
     */
    fun markProductPicked(index: Int, foundQuantity: Int) {
        _viewState.update { state ->
            val products = state.task.products.toMutableList()
            val product = products.getOrNull(index) ?: return@update state
            products[index] = product.copy(pickedQuantity = foundQuantity)
            state.copy(task = state.task.copy(products = products))
        }
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}
