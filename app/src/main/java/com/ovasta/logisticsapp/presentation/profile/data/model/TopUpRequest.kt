package com.ovasta.logisticsapp.presentation.profile.data.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.ovasta.logisticsapp.R

/** A request to add money to the agent's wallet, sent with a photo proving the transfer. */
data class TopUpRequest(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("amount") val amount: Double? = null,
    // Set when the reviewer approved a different amount than requested
    @SerializedName("adjusted_amount") val adjustedAmount: Double? = null,
    @SerializedName("adjustment_reason") val adjustmentReason: String? = null,
    @SerializedName("status") val status: Int? = null,
    @SerializedName("rejection_reason") val rejectionReason: String? = null,
    @SerializedName("processed_at") val processedAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)

/** The [TopUpRequest.status] ids sent by the backend. */
enum class TopUpStatus(val id: Int, @StringRes val labelRes: Int) {
    PENDING(1, R.string.top_up_status_pending),
    APPROVED(2, R.string.top_up_status_approved),
    REJECTED(3, R.string.top_up_status_rejected);

    companion object {
        fun fromId(id: Int?): TopUpStatus? = entries.firstOrNull { it.id == id }
    }
}
