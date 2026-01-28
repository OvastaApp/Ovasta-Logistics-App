package com.maxab.basemodule.core.setting.model

import com.google.gson.annotations.SerializedName

data class OrderSku(
    @SerializedName("unit") val unit: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("image") val image: String
)
