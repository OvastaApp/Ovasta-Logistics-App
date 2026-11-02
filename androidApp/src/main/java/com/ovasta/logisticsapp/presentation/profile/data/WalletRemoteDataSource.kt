package com.ovasta.logisticsapp.presentation.profile.data

import android.content.Context
import android.net.Uri
import com.ovasta.logisticsapp.base.ext.compressImageToJpeg
import com.ovasta.logisticsapp.base.ext.formatDecimal
import com.ovasta.logisticsapp.data.PaginatedData
import com.ovasta.logisticsapp.presentation.profile.data.model.TopUpRequest
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/** Largest top-up proof we upload: under 100 KB (99,999 bytes, so it fits either KB definition). */
private const val MAX_PROOF_IMAGE_BYTES = 99_999

class WalletRemoteDataSource(
    private val api: WalletApi,
    private val context: Context,
) : IWalletRemoteDataSource {

    override suspend fun getWalletTransactions(page: Int): PaginatedData<WalletTransaction> =
        api.getWalletTransactions(page).data ?: PaginatedData()

    override suspend fun getTopUpRequests(page: Int): PaginatedData<TopUpRequest> =
        api.getTopUpRequests(page).data ?: PaginatedData()

    override suspend fun createTopUpRequest(amount: Double, proof: Uri) {
        api.createTopUpRequest(
            amount = amount.formatDecimal(maxDecimals = 2).toRequestBody("text/plain".toMediaType()),
            proof = proof.toProofPart()
        )
    }

    /** Compresses the image off the main thread, under 100 KB, into the multipart `proof` field. */
    private suspend fun Uri.toProofPart(): MultipartBody.Part {
        val bytes = withContext(Dispatchers.IO) {
            context.compressImageToJpeg(this@toProofPart, maxBytes = MAX_PROOF_IMAGE_BYTES)
        }
        return MultipartBody.Part.createFormData(
            "proof",
            "proof.jpg",
            bytes.toRequestBody("image/jpeg".toMediaType())
        )
    }
}
