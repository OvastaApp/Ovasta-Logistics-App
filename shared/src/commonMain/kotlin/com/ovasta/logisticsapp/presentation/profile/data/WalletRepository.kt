package com.ovasta.logisticsapp.presentation.profile.data

import com.ovasta.logisticsapp.data.PaginatedData
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction

class WalletRepository(
    private val walletRemoteDataSource: IWalletRemoteDataSource,
) : IWalletRepository {
    override suspend fun getWalletTransactions(page: Int): PaginatedData<WalletTransaction> =
        walletRemoteDataSource.getWalletTransactions(page)

    override suspend fun getTopUpRequests(page: Int): PaginatedData<TopUpRequest> =
        walletRemoteDataSource.getTopUpRequests(page)

    override suspend fun createTopUpRequest(amount: Double, proof: ByteArray) =
        walletRemoteDataSource.createTopUpRequest(amount, proof)
}
