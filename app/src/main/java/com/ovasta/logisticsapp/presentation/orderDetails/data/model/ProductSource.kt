package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProductSource(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,
)
