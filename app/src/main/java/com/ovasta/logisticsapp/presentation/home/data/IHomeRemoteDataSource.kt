package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
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

    suspend fun changePartnerStatus(isOnline: Boolean)

    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>

    suspend fun getPartnerStatistics(): ApiResponse<PartnerStatistics>

}