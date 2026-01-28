package com.ovasta.logisticsapp.presentation.home.data.model

sealed class TaskStatus(val id: Int) {
    data object Pending : TaskStatus(1)
    data object InProgress : TaskStatus(2)
    data object Completed : TaskStatus(3)
    data object Cancelled : TaskStatus(4)
}
