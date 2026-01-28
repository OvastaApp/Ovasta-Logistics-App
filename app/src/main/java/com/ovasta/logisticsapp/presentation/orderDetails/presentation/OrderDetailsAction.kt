package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask


sealed interface OrderDetailsItemActions {
    data class ShowTaskDetails(val taskId: Int, val retailerId: Int) : OrderDetailsItemActions
    data class OpenDirection(val lat: Float, val lng: Float) : OrderDetailsItemActions
    data class ShowOtherTaskDialog(val taskId: Int, val retailerId: Int) : OrderDetailsItemActions
    data object ShowCompletedTaskDialog : OrderDetailsItemActions
    data class OpenContactBottomSheet(val homeTask: HomeTask): OrderDetailsItemActions
    data class TaskClicked(val homeTask: HomeTask) : OrderDetailsItemActions
    data class CallRetailer(val retailerPhone:String): OrderDetailsItemActions
    data class WhatsAppRetailer(val retailerPhone:String): OrderDetailsItemActions
    data class CopyPhone(val retailerPhone:String): OrderDetailsItemActions
    data object DismissContactBottomSheet: OrderDetailsItemActions
}

sealed interface OrderDetailsAction {
    data object LoadTasks : OrderDetailsAction
    data class OnSearchKeyChange(val searchKey:String) : OrderDetailsAction
    data object ClearToastMessage : OrderDetailsAction

    data object OnSearchTriggered : OrderDetailsAction

}