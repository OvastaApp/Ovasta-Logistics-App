package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ProductSource(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String? = null,
)
