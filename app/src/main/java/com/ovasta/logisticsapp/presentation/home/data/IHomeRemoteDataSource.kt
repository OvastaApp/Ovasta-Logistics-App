package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow

interface IHomeRemoteDataSource {
    suspend fun getAssignedTasks(
        userId: Int,
        districtId: Int,
        userType: String
    ): Flow<List<HomeTask>>

    suspend fun logLocation(
        userId: Int, districtId: Int, latitude: Double, longitude: Double
    )

}