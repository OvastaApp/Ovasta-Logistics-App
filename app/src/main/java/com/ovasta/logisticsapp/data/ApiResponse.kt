package com.ovasta.logisticsapp.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status")
    @Expose
    var status: Int,
    @SerializedName("message")
    @Expose
    var message: String,
    @SerializedName("data")
    @Expose
    var data: T,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("token")
    val token: String,
) {
    override fun toString(): String {
        return "ApiResponse(success=$status, message=$message, data=$data)"
    }
}
