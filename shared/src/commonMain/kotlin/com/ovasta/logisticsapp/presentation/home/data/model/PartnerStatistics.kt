package com.ovasta.logisticsapp.presentation.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnerStatistics(
    @SerialName("withdraw_transactions_sum") var withdrawTransactionsSum: Double? = null,
    @SerialName("delivery_profit_sum") var deliveryProfitSum: Double? = null,
    @SerialName("orders_count") var ordersCount: Int? = null,
    @SerialName("incentives") var incentives: Incentives? = Incentives()

)

@Serializable
data class Incentives(

    @SerialName("month") var month: String? = null,
    @SerialName("completed_orders") var completedOrders: Int? = null,
    @SerialName("total_delivery_value") var totalDeliveryValue: Double? = null,
    @SerialName("highest_achieved_milestone") var highestAchievedMilestone: String? = null,
    @SerialName("current_bonus_percentage") var currentBonusPercentage: Double? = null,
    @SerialName("current_bonus_amount") var currentBonusAmount: Double? = null,
    @SerialName("milestones") var milestones: ArrayList<Milestones> = arrayListOf()

)

@Serializable
data class Milestones(
    @SerialName("target_orders") var targetOrders: Int? = null,
    @SerialName("bonus_percentage") var bonusPercentage: Double? = null,
    @SerialName("real_percentage") var realPercentage: Double? = null,
    @SerialName("is_achieved") var isAchieved: Boolean? = null,
    @SerialName("remaining_orders") var remainingOrders: Int? = null,
    @SerialName("progress_percentage") var progressPercentage: Double? = null,
    @SerialName("bonus_amount") var bonusAmount: Double? = null
)