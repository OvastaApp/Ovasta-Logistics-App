package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask

data class OrderDetailsViewState(
    val tasks: List<HomeTask> = emptyList(),
    val filteredTasks: List<HomeTask> = emptyList(),
    val error: ComposeUIException? = null,
    val showToastMessage: Int? = null,
    val isLoading: Boolean = false,

    )