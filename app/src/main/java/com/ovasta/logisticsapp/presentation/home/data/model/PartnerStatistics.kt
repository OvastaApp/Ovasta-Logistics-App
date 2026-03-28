package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class PartnerStatistics(
    @SerializedName("Withdraw_transactions_sum") val withdrawTransactionsSum: Double? = null,
    @SerializedName("delivery_profit_sum") val deliveryProfitSum: Double? = null,
    @SerializedName("orders_count") val ordersCount: Int? = null,
    @SerializedName("target_orders_count") val targetOrdersCount: Int? = null,
    @SerializedName("target_end_date") val targetEndDate: String? = null
)