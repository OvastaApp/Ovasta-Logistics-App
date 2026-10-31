package com.ovasta.logisticsapp.presentation.profile.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.PaginatedData
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface WalletApi {
    /** Returns one page of the wallet's transactions, newest first. */
    @GET("wallet/transactions")
    suspend fun getWalletTransactions(@Query("page") page: Int): ApiResponse<PaginatedData<WalletTransaction>?>

    /** Returns one page of the agent's top-up requests, newest first. */
    @GET("topup-requests")
    suspend fun getTopUpRequests(@Query("page") page: Int): ApiResponse<PaginatedData<TopUpRequest>?>

    /** Asks for [amount] to be added to the wallet, with a photo of the transfer as [proof]. */
    @Multipart
    @POST("topup-requests")
    suspend fun createTopUpRequest(
        @Part("amount") amount: RequestBody,
        @Part proof: MultipartBody.Part
    ): ApiResponse<Any>
}
