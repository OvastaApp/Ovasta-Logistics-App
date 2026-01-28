package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    suspend fun getAssignedTasks(
        userId: Int,
        branchId: Int,
        userType: String
    ): Flow<List<HomeTask>>
}