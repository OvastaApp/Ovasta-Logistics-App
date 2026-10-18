package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import android.net.Uri
import androidx.annotation.StringRes
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
        // This ViewModel is reused for every order in the session, so drop the previous order's
        // state (including isOrderDelivered, which would otherwise close the screen right away)
        _viewState.value = TaskDetailsViewState()
        listenToOrder()
    }

    /**
     * (Re)starts the live listener on the current order. Calling it again re-reads the order
     * while keeping what is already on screen, e.g. after the backend changed it.
     */
    private fun listenToOrder() {
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
     * Persists an edit to a single product's price and/or quantity. On an editable order
     * ([HomeTask.isEditable], after an approved edit request) it goes through the backend
     * (`sales-orders/{id}/update`, price and quantity only); otherwise it rewrites the product in
     * Firestore. Either way the snapshot listener started in [getTaskDetails] reflects the change
     * back into [viewState].
     *
     * @param index position of the product within the current product list.
     * @param newPrice the new unit price; when null the existing price is kept.
     * @param newQuantity the new quantity; when null the existing quantity is kept.
     * @param newSource the new source name; when null the existing source is kept.
     */
    fun updateProduct(
        index: Int,
        newPrice: Double?,
        newQuantity: Double?,
        newSource: String? = null
    ) {
        viewModelScope.launch {
            val products = _viewState.value.task.products.toMutableList()
            val product = products.getOrNull(index) ?: return@launch

            val price = newPrice ?: product.itemPrice ?: 0.0
            val quantity = newQuantity ?: product.quantity ?: 0.0

            products[index] = product.copy(
                itemPrice = price,
                quantity = quantity,
                totalPrice = price * quantity,
                source = newSource ?: product.source
            )

            setComposeUILoading(true)
            kotlin.runCatching {
                if (_viewState.value.task.isEditable) {
                    orderDetailsRepository.updateSalesOrderProduct(
                        orderId = taskId,
                        catalogProduct = product.catalogProduct ?: false,
                        productId = product.requireProductId(),
                        quantity = quantity,
                        price = price
                    )
                } else {
                    orderDetailsRepository.updateProducts(
                        districtId = settingsRepository.getUseData()?.districtId ?: 0,
                        taskId = taskId,
                        products = products
                    )
                }
            }.onSuccess {
                setComposeUILoading(false)
                emitToastEvent(ToastEvent.ResourceToastEvent(R.string.product_updated_successfully))
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    /**
     * Removes a single product from the order. On an editable order ([HomeTask.isEditable]) it
     * goes through the backend (`sales-orders/{id}/delete-product`); otherwise it rewrites the
     * order's products in Firestore without the product at [index]. The snapshot listener started
     * in [getTaskDetails] reflects the removal back into [viewState].
     *
     * @param index position of the product to delete within the current product list.
     */
    fun deleteProduct(index: Int) {
        viewModelScope.launch {
            val products = _viewState.value.task.products.toMutableList()
            if (index !in products.indices) return@launch
            val product = products.removeAt(index)

            setComposeUILoading(true)
            kotlin.runCatching {
                if (_viewState.value.task.isEditable) {
                    orderDetailsRepository.deleteSalesOrderProduct(
                        orderId = taskId,
                        catalogProduct = product.catalogProduct ?: false,
                        productId = product.requireProductId()
                    )
                } else {
                    orderDetailsRepository.updateProducts(
                        districtId = settingsRepository.getUseData()?.districtId ?: 0,
                        taskId = taskId,
                        products = products
                    )
                }
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
    fun markProductPicked(index: Int, foundQuantity: Double) {
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
     * Marks the order as delivered on the backend (`sales-orders/{id}/deliver`) with the cash
     * amount collected from the client, which may be less or more than the order total.
     * On success [TaskDetailsViewState.isOrderDelivered] is set so the screen can navigate back home.
     */
    fun deliverOrder(receivedAmount: Double) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                orderDetailsRepository.deliverSalesOrder(
                    orderId = taskId,
                    collectedAmount = receivedAmount
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

    /** Asks the backend to make the order editable, attaching [image] (picked or captured). */
    fun requestOrderEdit(image: Uri) = uploadOrderImage(R.string.edit_request_sent) {
        orderDetailsRepository.requestOrderEdit(orderId = taskId, image = image)
    }

    /** Uploads the order's receipt [image] (picked or captured). */
    fun uploadOrderReceipt(image: Uri) = uploadOrderImage(R.string.receipt_uploaded) {
        orderDetailsRepository.uploadOrderImage(orderId = taskId, image = image)
    }

    private fun uploadOrderImage(@StringRes successMessage: Int, upload: suspend () -> Unit) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching { upload() }
                .onSuccess {
                    setComposeUILoading(false)
                    emitToastEvent(ToastEvent.ResourceToastEvent(successMessage))
                    // The backend may have changed the order (e.g. is_edit_allowed): re-read it
                    listenToOrder()
                }.onFailure {
                    updateViewStateWithFail(it)
                }
        }
    }

    /** The product's id as the sales-order edit APIs expect it (its Firestore `product_id`). */
    private fun FirebaseProduct.requireProductId(): Int =
        requireNotNull(productId) { "Product '$name' has no product_id" }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }

    companion object {
        /** Backend display name stored in the order's `status_name` field. */
        private const val ON_THE_WAY_STATUS_NAME = "On The Way"
    }
}
