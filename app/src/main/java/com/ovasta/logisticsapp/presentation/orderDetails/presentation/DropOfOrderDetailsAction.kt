package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import com.ovasta.logisticsapp.presentation.orderDetails.data.ProductModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.ProductUnitModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.ReasonModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.TaskStatusModel

sealed interface DropOfOrderDetailsAction {

    object OnBackPressed : DropOfOrderDetailsAction

    object ReloadTaskDetails : DropOfOrderDetailsAction

    object ReloadTaskConfig : DropOfOrderDetailsAction

    object OnChangeStatusClick : DropOfOrderDetailsAction

    object OnReceiveAmountClick : DropOfOrderDetailsAction

    data class OnChangeProductQuantity(
        val productIndex: Int,
        val category: String,
        val product: ProductModel,
        val quantityChange: Int
    ) : DropOfOrderDetailsAction

    object OnInfoClick: DropOfOrderDetailsAction

    data class ChangePaymentInfoVisibility(
        val isVisible: Boolean
    ) : DropOfOrderDetailsAction

    object DismissStatusDialogs : DropOfOrderDetailsAction

    data class OnSelectTaskStatus(
        val selectedStatus: TaskStatusModel,
    ) : DropOfOrderDetailsAction

    object BackToTaskStatus : DropOfOrderDetailsAction

    data class InitScreen(val taskId: Int, val retailerId: Int) : DropOfOrderDetailsAction

    data class OnDelayConfirmed(val newDate: String, val reason: ReasonModel) : DropOfOrderDetailsAction

    data class OnFailTask(val selectedReason: ReasonModel) : DropOfOrderDetailsAction

    data class OnDelaySameDay(val selectedReason: ReasonModel) : DropOfOrderDetailsAction

    object NavigateToUpSellingProductsScreen : DropOfOrderDetailsAction

    class OnPlusProductQuantityClicked(
        val category: String,
        val productIndex: Int,
        val product: ProductModel,
        val unitIndex: Int,
        val unit: ProductUnitModel
    ) : DropOfOrderDetailsAction

    class OnMinusProductQuantityClicked(
        val category: String,
        val productIndex: Int,
        val product: ProductModel,
        val unitIndex: Int,
        val unit: ProductUnitModel
    ) : DropOfOrderDetailsAction

    class OnPlusBundleQuantityClicked(
        val bundle: ProductModel,
        val bundleIndex: Int,
        val category: String
    ) : DropOfOrderDetailsAction

    class OnMinusBundleQuantityClicked(
        val bundle: ProductModel,
        val bundleIndex: Int,
        val category: String
    ) : DropOfOrderDetailsAction

    object OnSaveUpSellingClick : DropOfOrderDetailsAction

    object DismissAddProductsDialog : DropOfOrderDetailsAction

    object OnConfirmClick : DropOfOrderDetailsAction
}