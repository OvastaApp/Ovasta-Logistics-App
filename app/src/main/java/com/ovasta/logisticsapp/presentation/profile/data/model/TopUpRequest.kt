package com.ovasta.logisticsapp.presentation.profile.data.model

import androidx.annotation.StringRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.ovasta.logisticsapp.R

/** A request to add money to the agent's wallet, sent with a photo proving the transfer. */
@Serializable
data class TopUpRequest(
    @SerialName("id") val id: Int? = null,
    @SerialName("amount") val amount: Double? = null,
    // Set when the reviewer approved a different amount than requested
    @SerialName("adjusted_amount") val adjustedAmount: Double? = null,
    @SerialName("adjustment_reason") val adjustmentReason: String? = null,
    @SerialName("status") val status: Int? = null,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("processed_at") val processedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
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
