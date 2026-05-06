package com.ovasta.logisticsapp.presentation.home.data.model

sealed class OrderSteps {

    object Pending : OrderSteps()
    object Assigned : OrderSteps()
    object Picked : OrderSteps()
    object Delivered : OrderSteps()
    object Canceled : OrderSteps()

    companion object {
        fun fromStatusId(statusId: Int): OrderSteps {
            return when (statusId) {
                1 -> Pending
                2 -> Assigned
                3 -> Picked
                4 -> Delivered
                5 -> Canceled
                else -> throw kotlin.IllegalArgumentException("Unknown statusId: $statusId")
            }
        }

        fun toStatus(status: OrderSteps): Int {
            return when (status) {
                is Pending -> 1
                is Assigned -> 2
                is Picked -> 3
                is Delivered -> 4
                is Canceled -> 5
            }
        }
    }

}