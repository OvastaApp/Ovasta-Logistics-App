package com.ovasta.logisticsapp.presentation.profile.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.PaginatedData
import com.ovasta.logisticsapp.data.remote.postJpeg
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonElement

class WalletApi(private val client: HttpClient) {

    /** Returns one page of the wallet's transactions, newest first. */
    suspend fun getWalletTransactions(page: Int): ApiResponse<PaginatedData<WalletTransaction>?> =
        client.get("wallet/transactions") { parameter("page", page) }.body()

    /** Returns one page of the agent's top-up requests, newest first. */
    suspend fun getTopUpRequests(page: Int): ApiResponse<PaginatedData<TopUpRequest>?> =
        client.get("topup-requests") { parameter("page", page) }.body()

    /** Asks for [amount] to be added to the wallet, with a JPEG of the transfer as [proof]. */
    suspend fun createTopUpRequest(amount: String, proof: ByteArray): ApiResponse<JsonElement> =
        client.postJpeg("topup-requests", "proof", "proof.jpg", proof, fields = mapOf("amount" to amount)).body()
}
