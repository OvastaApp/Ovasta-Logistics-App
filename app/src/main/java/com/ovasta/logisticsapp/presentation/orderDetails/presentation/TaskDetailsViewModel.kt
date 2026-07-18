package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.ext.ToastEvent
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.OrderSteps
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
     * @param newSource the new source name; when null the existing source is kept.
     */
    fun updateProduct(index: Int, newPrice: Int?, newQuantity: Int?, newSource: String? = null) {
        viewModelScope.launch {
            val products = _viewState.value.task.products.toMutableList()
            val product = products.getOrNull(index) ?: return@launch

            val price = newPrice ?: product.itemPrice ?: 0
            val quantity = newQuantity ?: product.quantity ?: 0

            products[index] = product.copy(
                itemPrice = price,
                quantity = quantity,
                totalPrice = price.toDouble() * quantity,
                source = newSource ?: product.source
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
     * Marks a product as picked with the given found quantity. The value is applied to
     * [viewState] immediately so the required-vs-found UI updates without waiting for the
     * network, then persisted to the product's `picked_quantity` field in Firestore so the
     * action survives restarts and later snapshot emissions.
     */
    fun markProductPicked(index: Int, foundQuantity: Int) {
        _viewState.update { state ->
            val products = state.task.products.toMutableList()
            val product = products.getOrNull(index) ?: return@update state
            products[index] = product.copy(pickedQuantity = foundQuantity)
            state.copy(task = state.task.copy(products = products))
        }

        viewModelScope.launch {
            kotlin.runCatching {
                orderDetailsRepository.updateProducts(
                    districtId = settingsRepository.getUseData()?.districtId ?: 0,
                    taskId = taskId,
                    products = _viewState.value.task.products
                )
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    /**
     * Loads the sources the given product can be moved to. Called lazily when the user opens the
     * source dropdown in the edit sheet; results land in
     * [TaskDetailsViewState.availableSources].
     */
    fun loadProductSources(product: FirebaseProduct) {
        viewModelScope.launch {
            _viewState.update { it.copy(isSourcesLoading = true, availableSources = emptyList()) }
            kotlin.runCatching {
                orderDetailsRepository.getProductSources(
                    productId = product.productId ?: product.mainSysId ?: 0
                )
            }.onSuccess { sources ->
                _viewState.update {
                    it.copy(isSourcesLoading = false, availableSources = sources)
                }
            }.onFailure {
                _viewState.update { it.copy(isSourcesLoading = false) }
                updateViewStateWithFail(it)
            }
        }
    }

    /**
     * Moves the order from Assigned to on-the-way-to-client ([OrderSteps.Picked]). Callers must
     * only invoke this once every remaining product has been acted on (picked fully/partially or
     * deleted) — the UI enforces this by disabling the change-status button otherwise.
     */
    fun changeOrderStatusToOnTheWay() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                orderDetailsRepository.updateOrderStatus(
                    districtId = settingsRepository.getUseData()?.districtId ?: 0,
                    taskId = taskId,
                    statusId = OrderSteps.toStatus(OrderSteps.Picked),
                    statusName = ON_THE_WAY_STATUS_NAME
                )
            }.onSuccess {
                setComposeUILoading(false)
                emitToastEvent(ToastEvent.ResourceToastEvent(R.string.order_status_updated_successfully))
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    /**
     * Marks the order as delivered with the cash amount collected from the client. Callers must
     * validate that [receivedAmount] is at least the order total — the receive-amount sheet
     * enforces this before enabling its confirm button. On success
     * [TaskDetailsViewState.isOrderDelivered] is set so the screen can navigate back home.
     */
    fun deliverOrder(receivedAmount: Double) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                orderDetailsRepository.updateOrderStatus(
                    districtId = settingsRepository.getUseData()?.districtId ?: 0,
                    taskId = taskId,
                    statusId = OrderSteps.toStatus(OrderSteps.Delivered),
                    statusName = DELIVERED_STATUS_NAME,
                    receivedAmount = receivedAmount
                )
            }.onSuccess {
                setComposeUILoading(false)
                emitToastEvent(ToastEvent.ResourceToastEvent(R.string.order_delivered_successfully))
                _viewState.update { it.copy(isOrderDelivered = true) }
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }

    companion object {
        /** Backend display names stored in the order's `status_name` field. */
        private const val ON_THE_WAY_STATUS_NAME = "On The Way"
        private const val DELIVERED_STATUS_NAME = "Delivered"
    }
}
