package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow

interface IOrderDetailsRemoteDataSource {
    suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask>

}