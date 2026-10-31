package com.ovasta.logisticsapp.presentation.profile.data.model

import com.google.gson.annotations.SerializedName

data class WalletTransaction(
    @SerializedName("id") val id: Int? = null,
    // Signed: positive adds to the wallet, negative takes from it
    @SerializedName("amount") val amount: Double? = null,
    // e.g. "topup_credit", "delivery_debit", "welcome_balance"
    @SerializedName("type") val type: String? = null,
    @SerializedName("description") val description: String? = null,
    // The delivery order a delivery debit was charged for
    @SerializedName("delivery_order_id") val deliveryOrderId: Int? = null,
    @SerializedName("created_at") val createdAt: String? = null,
) {
    /** Whether the transaction added money to the wallet. */
    val isCredit: Boolean get() = (amount ?: 0.0) >= 0.0
}
