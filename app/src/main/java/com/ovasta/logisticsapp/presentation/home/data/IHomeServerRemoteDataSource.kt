package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask
import kotlinx.coroutines.flow.Flow

interface IHomeServerRemoteDataSource {

    suspend fun changePartnerStatus(isOnline: Boolean)

    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>

    suspend fun getPartnerStatistics(
        month: Int,
        year: Int,
    ): ApiResponse<PartnerStatistics>

}