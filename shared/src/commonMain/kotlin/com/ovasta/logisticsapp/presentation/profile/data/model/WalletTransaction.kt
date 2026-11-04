package com.ovasta.logisticsapp.presentation.profile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletTransaction(
    @SerialName("id") val id: Int? = null,
    // Signed: positive adds to the wallet, negative takes from it
    @SerialName("amount") val amount: Double? = null,
    // e.g. "topup_credit", "delivery_debit", "welcome_balance"
    @SerialName("type") val type: String? = null,
    @SerialName("description") val description: String? = null,
    // The delivery order a delivery debit was charged for
    @SerialName("delivery_order_id") val deliveryOrderId: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
) {
    /** Whether the transaction added money to the wallet. */
    val isCredit: Boolean get() = (amount ?: 0.0) >= 0.0
}
