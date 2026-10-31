package com.ovasta.logisticsapp.presentation.profile.data

import android.net.Uri
import com.ovasta.logisticsapp.data.PaginatedData
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction

interface IWalletRepository {
    suspend fun getWalletTransactions(page: Int): PaginatedData<WalletTransaction>

    suspend fun getTopUpRequests(page: Int): PaginatedData<TopUpRequest>

    suspend fun createTopUpRequest(amount: Double, proof: Uri)
}
