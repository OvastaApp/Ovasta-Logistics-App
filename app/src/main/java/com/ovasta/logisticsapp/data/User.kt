package com.ovasta.logisticsapp.data

import com.google.gson.annotations.SerializedName
import com.ovasta.logisticsapp.base.UserType
import kotlinx.serialization.Serializable

@Serializable
class User(
    @SerializedName("id") val id: Int,
    @SerializedName("delivery_id") val deliveryId: Int,
    @SerializedName("district_id") val districId: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("type_id") var userTypeId: Int?,
    @SerializedName("available") var available: Boolean?,
   var token: String?,
)

