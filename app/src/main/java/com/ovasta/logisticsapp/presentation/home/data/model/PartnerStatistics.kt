package com.ovasta.logisticsapp.presentation.home.data.model

import com.google.gson.annotations.SerializedName

data class PartnerStatistics(
    @SerializedName("withdraw_transactions_sum") var withdrawTransactionsSum: Double? = null,
    @SerializedName("delivery_profit_sum") var deliveryProfitSum: Double? = null,
    @SerializedName("orders_count") var ordersCount: Int? = null,
    @SerializedName("incentives") var incentives: Incentives? = Incentives()

)

data class Incentives(

    @SerializedName("month") var month: String? = null,
    @SerializedName("completed_orders") var completedOrders: Int? = null,
    @SerializedName("total_delivery_value") var totalDeliveryValue: Double? = null,
    @SerializedName("highest_achieved_milestone") var highestAchievedMilestone: String? = null,
    @SerializedName("current_bonus_percentage") var currentBonusPercentage: Double? = null,
    @SerializedName("current_bonus_amount") var currentBonusAmount: Double? = null,
    @SerializedName("milestones") var milestones: ArrayList<Milestones> = arrayListOf()

)

data class Milestones(
    @SerializedName("target_orders") var targetOrders: Int? = null,
    @SerializedName("bonus_percentage") var bonusPercentage: Double? = null,
    @SerializedName("real_percentage") var realPercentage: Double? = null,
    @SerializedName("is_achieved") var isAchieved: Boolean? = null,
    @SerializedName("remaining_orders") var remainingOrders: Int? = null,
    @SerializedName("progress_percentage") var progressPercentage: Double? = null,
    @SerializedName("bonus_amount") var bonusAmount: Double? = null
)