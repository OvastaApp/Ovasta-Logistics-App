package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource

data class TaskDetailsViewState(
    val task: HomeTask = HomeTask(),
    val error: ComposeUIException? = null,
    val showToastMessage: Int? = null,
    val isLoading: Boolean = false,
    val categoryToProducts: Map<String, List<FirebaseProduct>> = mapOf(),
    val currency: String = "EGP",
    /** Sources the product currently being edited can be moved to (loaded on demand). */
    val availableSources: List<ProductSource> = emptyList(),
    val isSourcesLoading: Boolean = false,
    /** Set once the order is successfully marked delivered; the screen then navigates back. */
    val isOrderDelivered: Boolean = false,
)
