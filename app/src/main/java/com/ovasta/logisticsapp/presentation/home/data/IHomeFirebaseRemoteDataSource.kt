package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import kotlinx.coroutines.flow.Flow

interface IHomeFirebaseRemoteDataSource {
    suspend fun getAssignedOrders(
        userId: Int,
        districtId: Int
    ): Flow<List<HomeTask>>

    suspend fun listenToNewDeliveryTasks(
        userId: Int,
        districtId: Int
    ): Flow<List<DeliveryTask>>


    suspend fun logLocation(
        userId: Int, districtId: Int, latitude: Double, longitude: Double
    )

}