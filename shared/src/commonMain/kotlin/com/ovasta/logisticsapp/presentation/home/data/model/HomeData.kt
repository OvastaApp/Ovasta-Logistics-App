package com.ovasta.logisticsapp.presentation.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Response of `home`: the courier's current account and wallet state. */
@Serializable
data class HomeData(
    @SerialName("courier") val courier: HomeCourier? = null,
    @SerialName("wallet_balance") val walletBalance: Double? = null,
    // The lowest the wallet balance may go (a negative number)
    @SerialName("max_wallet_negative_amount") val maxWalletNegativeAmount: Double? = null,
    @SerialName("daily_profit") val dailyProfit: Double? = null,
    @SerialName("monthly_profit") val monthlyProfit: Double? = null,
    // Message to show the courier about their wallet, e.g. a low balance; null when there is none
    @SerialName("wallet_warning") val walletWarning: String? = null,
)

@Serializable
data class HomeCourier(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("is_outsource") val isOutsource: Boolean? = null,
)
