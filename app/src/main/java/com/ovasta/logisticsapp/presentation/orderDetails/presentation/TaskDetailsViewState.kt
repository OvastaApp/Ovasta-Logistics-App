package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask

data class TaskDetailsViewState(
    val task: HomeTask = HomeTask(),
    val error: ComposeUIException? = null,
    val showToastMessage: Int? = null,
    val isLoading: Boolean = false,
    val categoryToProducts: Map<String, List<FirebaseProduct>> = mapOf(),
    val currency: String = "EGP",
)
