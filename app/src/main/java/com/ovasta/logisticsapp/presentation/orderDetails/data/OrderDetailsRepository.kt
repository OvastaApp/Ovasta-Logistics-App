package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow

class OrderDetailsRepository(
    private val homeRemoteDataSource: IOrderDetailsRemoteDataSource,
) : IOrderDetailsRepository {
    override suspend fun getTaskDetails(
        districId: Int,
        taskId: Int
    ): Flow<HomeTask> =
        homeRemoteDataSource.getTaskDetails(districId, taskId)
}