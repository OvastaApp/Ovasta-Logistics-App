package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.ovasta.logisticsapp.presentation.orderDetails.data.ProductModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class DropOfTaskDetailsModel(
    @SerializedName("task_id")
    val taskId: Int,

    @SerializedName("shipment_id")
    val shipmentId: Int,

    @SerializedName("vendor_id")
    val vendorId: Int,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("discounts")
    val discounts: Double,

    @SerializedName("prepaid_amount")
    val prepaidAmount: Double,

    @SerializedName("to_be_collected_amount")
    val toBeCollectedAmount: Double,

    @SerializedName("tags")
    val tags: List<String>,

    @SerializedName("items")
    val products: List<ProductModel>,

    @SerializedName("upselling_items_count")
    val upsellingItemsCount: Int?,
    @SerializedName("has_pending_transaction")
    val hasPendingTransaction: Boolean?,
) : Parcelable