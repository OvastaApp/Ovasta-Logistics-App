package com.ovasta.logisticsapp.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

// Comes from the login API and is also kept in SessionPreferences, which stored the camelCase
// property names before; @JsonNames still reads those so saved sessions survive the update
@OptIn(ExperimentalSerializationApi::class)
@Serializable
class User(
    @SerialName("id") val id: Int = 0,
    @SerialName("delivery_id") @JsonNames("deliveryId") val deliveryId: Int = 0,
    @SerialName("district_id") @JsonNames("districtId") val districtId: Int = 0,
    @SerialName("name") val name: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("email") var email: String? = null,
    @SerialName("type_id") @JsonNames("userTypeId") var userTypeId: Int? = null,
    @SerialName("available") var available: Boolean? = null,
    @SerialName("is_outsource") @JsonNames("isOutsource") val isOutsource: Boolean = false,
    var token: String? = null,
)
