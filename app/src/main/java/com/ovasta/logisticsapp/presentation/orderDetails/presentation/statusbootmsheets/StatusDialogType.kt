package com.ovasta.logisticsapp.presentation.orderDetails.presentation.statusbootmsheets

sealed class StatusDialogType(val dialogState: StatusDialogState) {

    class StatusType(dialogState: StatusDialogState) : StatusDialogType(dialogState)

    class ReasonType(
        dialogState: StatusDialogState,
    ) : StatusDialogType(dialogState)

    object None : StatusDialogType(StatusDialogState())
}