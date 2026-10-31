package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

/** Response of `home`: the courier's current account and wallet state. */
data class HomeData(
    @SerializedName("courier") val courier: HomeCourier? = null,
    @SerializedName("wallet_balance") val walletBalance: Double? = null,
    // The lowest the wallet balance may go (a negative number)
    @SerializedName("max_wallet_negative_amount") val maxWalletNegativeAmount: Double? = null,
    @SerializedName("daily_profit") val dailyProfit: Double? = null,
    @SerializedName("monthly_profit") val monthlyProfit: Double? = null,
    // Message to show the courier about their wallet, e.g. a low balance; null when there is none
    @SerializedName("wallet_warning") val walletWarning: String? = null,
)

data class HomeCourier(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("is_outsource") val isOutsource: Boolean? = null,
)
