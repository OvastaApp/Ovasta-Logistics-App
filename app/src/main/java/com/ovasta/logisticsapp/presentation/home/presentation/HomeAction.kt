package com.ovasta.logisticsapp.presentation.home.presentation

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask


sealed interface HomeItemActions {
    data class ShowTaskDetails(val taskId: Int, val retailerId: Int) : HomeItemActions
    data class OpenDirection(val lat: Double, val lng: Double) : HomeItemActions
    data class ShowOtherTaskDialog(val taskId: Int, val retailerId: Int) : HomeItemActions
    data object ShowCompletedTaskDialog : HomeItemActions
    data class OpenContactBottomSheet(val homeTask: HomeTask) : HomeItemActions
    data class TaskClicked(val taskId: Int) : HomeItemActions
    data class CallRetailer(val clientPhone: String) : HomeItemActions
    data class WhatsAppRetailer(val clientWhatsapp: String) : HomeItemActions
    data object DismissContactBottomSheet : HomeItemActions
    data class AcceptDeliveryTask(val orderId: Int) : HomeItemActions
    data class DismissNewTaskAlert(val orderId: Int) : HomeItemActions
}

sealed interface HomeScreenActions {
    data object LoadTasks : HomeScreenActions
    data object ClearToastMessage : HomeScreenActions

    data object RefreshTasks : HomeScreenActions

    object ToggleTracking : HomeScreenActions

    data class ChangeLogoutDialogStatus(val isVisible: Boolean) : HomeScreenActions
    object OnLogoutClicked : HomeScreenActions
    data class OnMonthYearFilterChanged(val month: Int, val year: Int) : HomeScreenActions
}