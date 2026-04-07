package com.ovasta.logisticsapp.presentation.orderDetails.presentation



sealed interface TaskDetailsAction {

    object OnBackPressed : TaskDetailsAction

    object ReloadTaskDetails : TaskDetailsAction

    object ReloadTaskConfig : TaskDetailsAction

    object OnChangeStatusClick : TaskDetailsAction



    object OnInfoClick: TaskDetailsAction

    data class ChangePaymentInfoVisibility(
        val isVisible: Boolean
    ) : TaskDetailsAction

    object DismissStatusDialogs : TaskDetailsAction


    object BackToTaskStatus : TaskDetailsAction

    data class InitScreen(val taskId: Int, val retailerId: Int) : TaskDetailsAction


    object OnSaveUpSellingClick : TaskDetailsAction

    object DismissAddProductsDialog : TaskDetailsAction

    object OnConfirmClick : TaskDetailsAction
}