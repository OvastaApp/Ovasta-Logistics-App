package com.ovasta.logisticsapp.data

import com.google.gson.annotations.SerializedName
import com.ovasta.logisticsapp.base.UserType
import kotlinx.serialization.Serializable

@Serializable
class User(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("userType") var userType: UserType?,
    @SerializedName("token") val token: String,
    @SerializedName("branch_id") val branchId: Int?,
    @SerializedName("firebase_token") val firebaseToken: String?,
)

