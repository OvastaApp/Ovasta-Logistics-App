package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import com.ovasta.logisticsapp.presentation.orderDetails.presentation.statusbootmsheets.StatusDialogType
import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.presentation.orderDetails.data.ProductModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DropOfTaskDetailsModel

data class DropOfOrderDetailsViewState(
    val isLoading: Boolean = false,
    val categoryToProducts: Map<String, List<ProductModel>> = mapOf(),
    val currency: String = "EGP",
    val showPaymentInfo: Boolean = false,
    val statusDialogType: StatusDialogType = StatusDialogType.None,
    val taskDetails: DropOfTaskDetailsModel? = null,
    val error: ComposeUIException? = null,
    val showUpdateButton: Boolean = false,
    val isSaveButtonAppear: Boolean = false,
    val showAddProductsDialog: Boolean = false
)