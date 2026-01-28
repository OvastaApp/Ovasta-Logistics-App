package com.ovasta.logisticsapp.presentation.orderDetails.presentation.statusbootmsheets

import com.ovasta.logisticsapp.presentation.orderDetails.data.ReasonModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.TaskStatusModel


data class StatusDialogState(
    val statusList: List<TaskStatusModel> = emptyList(),

    val reasonsList: List<ReasonModel> = emptyList(),

    val isRequestDelay: Boolean = false,

    val availableDays: List<String> = emptyList(),
)