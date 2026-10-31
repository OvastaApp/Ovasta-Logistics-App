package com.ovasta.logisticsapp.presentation.profile.data

import android.net.Uri
import com.ovasta.logisticsapp.data.PaginatedData
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction

interface IWalletRemoteDataSource {
    /** Fetches one page (starting at 1) of the wallet's transactions. */
    suspend fun getWalletTransactions(page: Int): PaginatedData<WalletTransaction>

    /** Fetches one page (starting at 1) of the agent's top-up requests. */
    suspend fun getTopUpRequests(page: Int): PaginatedData<TopUpRequest>

    /** Sends a top-up request for [amount], uploading [proof] (the transfer photo) with it. */
    suspend fun createTopUpRequest(amount: Double, proof: Uri)
}
