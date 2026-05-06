package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask
import kotlinx.coroutines.flow.Flow

interface IHomeFirebaseRemoteDataSource {
    suspend fun getAssignedTasks(
        userId: Int,
        districtId: Int
    ): Flow<List<HomeTask>>

    suspend fun getAvailableSellerOrders(
        userId: Int,
        districtId: Int
    ): Flow<List<SellerTask>>


    suspend fun logLocation(
        userId: Int, districtId: Int, latitude: Double, longitude: Double
    )

}