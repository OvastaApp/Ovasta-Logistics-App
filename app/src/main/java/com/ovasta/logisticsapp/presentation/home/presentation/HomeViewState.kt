package com.ovasta.logisticsapp.presentation.home.presentation

import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.AssignedDeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import java.time.LocalDate

data class HomeViewState(
    val appTasks: List<HomeTask> = emptyList(),
    val waitingDeliveryTasks: List<DeliveryTask> = emptyList(),
    val assignedDeliveryTasks: List<AssignedDeliveryTask> = emptyList(),
    val currentAlertTask: DeliveryTask? = null,
    val alertQueue: List<DeliveryTask> = emptyList(),
    val bottomSheetMinimized: Boolean = false,
    val error: ComposeUIException? = null,
    val showToastMessage: Int? = null,
    val isTracking: Boolean = false,
    val partnerStatistics: PartnerStatistics? = null,
    val monthFilter: Int = LocalDate.now().monthValue,
    val yearFilter: Int = LocalDate.now().year,
    val isLogoutDialogVisible: Boolean = false,
    val isTasksLoading: Boolean = true,
    val isAvailableTasksLoading: Boolean = true,
    val isLocationConsentDialogVisible: Boolean = false
)