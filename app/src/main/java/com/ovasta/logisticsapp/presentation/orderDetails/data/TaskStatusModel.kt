package com.ovasta.logisticsapp.presentation.orderDetails.data

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

@Keep
data class TaskStatusModel(
    val type: StatusType,

    @DrawableRes val icon: Int,

    @StringRes val title: Int,

    @StringRes val description: Int,

    val textAndIconColor: Color,

    var isSelected: Boolean = false,
) {

    enum class StatusType {
        DELAY,
        DELAY_SAME_DAY,
        CANCEL
    }
}