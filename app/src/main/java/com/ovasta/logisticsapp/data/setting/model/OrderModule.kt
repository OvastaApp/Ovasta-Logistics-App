package com.maxab.basemodule.core.setting.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Orders(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("discount") val discount: Double,
    //@SerializedName("order_status") val orderStatus: OrderStatus?,
    @SerializedName("coupons") val coupons: Double,
    @SerializedName("weight") val weight: Float,
    @SerializedName("partner_name") val partnerName: String,
    @SerializedName("status_id") val statusId: Int,
    @SerializedName("status_name") val statusName: String,
    @SerializedName("price") val oldPrice: Double = 0.0,
) : Parcelable

@Parcelize
data class OrderStatus(
    @SerializedName("status") val status: String, @SerializedName("id") val id: Int
) : Parcelable