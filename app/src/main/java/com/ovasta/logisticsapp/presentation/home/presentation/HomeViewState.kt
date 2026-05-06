package com.ovasta.logisticsapp.presentation.home.presentation

import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask
import java.time.LocalDate

data class HomeViewState(
    val tasks: List<HomeTask> = emptyList(),
    val sellerTasks: List<SellerTask> = emptyList(),
    val filteredTasks: List<HomeTask> = emptyList(),
    val error: ComposeUIException? = null,
    val showToastMessage: Int? = null,
    val isTracking: Boolean = false,
    val partnerStatistics: PartnerStatistics? = null,
    val monthFilter: Int = LocalDate.now().monthValue,
    val yearFilter: Int = LocalDate.now().year,
    val isLogoutDialogVisible: Boolean = false
)