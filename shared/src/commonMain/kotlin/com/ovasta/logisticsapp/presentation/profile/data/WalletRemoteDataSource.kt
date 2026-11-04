package com.ovasta.logisticsapp.presentation.profile.data

import com.ovasta.logisticsapp.base.ext.formatDecimal
import com.ovasta.logisticsapp.data.PaginatedData
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction

class WalletRemoteDataSource(
    private val api: WalletApi,
) : IWalletRemoteDataSource {

    override suspend fun getWalletTransactions(page: Int): PaginatedData<WalletTransaction> =
        api.getWalletTransactions(page).data ?: PaginatedData()

    override suspend fun getTopUpRequests(page: Int): PaginatedData<TopUpRequest> =
        api.getTopUpRequests(page).data ?: PaginatedData()

    override suspend fun createTopUpRequest(amount: Double, proof: ByteArray) {
        api.createTopUpRequest(amount = amount.formatDecimal(maxDecimals = 2), proof = proof)
    }
}
