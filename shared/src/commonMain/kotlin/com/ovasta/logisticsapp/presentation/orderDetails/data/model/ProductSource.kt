package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductSource(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String? = null,
)
