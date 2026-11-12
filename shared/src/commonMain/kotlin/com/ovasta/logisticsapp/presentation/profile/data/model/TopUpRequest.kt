package com.ovasta.logisticsapp.presentation.profile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

