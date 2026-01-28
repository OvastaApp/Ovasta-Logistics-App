package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow

interface IOrderDetailsRepository {
    suspend fun getTaskDetails(branchId: Int, taskId: Int): Flow<HomeTask>
}