package com.ovasta.logisticsapp.presentation.home.presentation

import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.AssignedDeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import java.time.LocalDate

data class HomeViewState(
    val userName: String = "",
    val appTasks: List<HomeTask> = emptyList(),
    val waitingDeliveryTasks: List<DeliveryTask> = emptyList(),
    val assignedDeliveryTasks: List<AssignedDeliveryTask> = emptyList(),
    val currentAlertTask: DeliveryTask? = null,
    val alertQueue: List<DeliveryTask> = emptyList(),
    val bottomSheetMinimized: Boolean = false,
    val error: ComposeUIException? = null,
    val showToastMessage: Int? = null,
    val isTracking: Boolean = false,
    /** From `home`; null until it loads. Outsource agents don't see the partner statistics. */
    val isOutsource: Boolean? = null,
    /** Message about the courier's wallet from `home`, shown above the orders; null for none. */
    val walletWarning: String? = null,
    val partnerStatistics: PartnerStatistics? = null,
    val monthFilter: Int = LocalDate.now().monthValue,
    val yearFilter: Int = LocalDate.now().year,
    val isTasksLoading: Boolean = true,
    val isAvailableTasksLoading: Boolean = true,
    val isLocationConsentDialogVisible: Boolean = false,
    /** Tapped order with no `received_at` yet; shows the "view order details?" confirmation. */
    val orderToStart: HomeTask? = null
)