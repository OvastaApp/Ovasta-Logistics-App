package com.ovasta.logisticsapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Defaults cover fields an endpoint leaves out; [data] is null when the response has none
@Serializable
data class ApiResponse<T>(
    @SerialName("status")
    var status: Int = 0,
    @SerialName("message")
    var message: String = "",
    @SerialName("data")
    var data: T? = null,
    @SerialName("last_page")
    val lastPage: Int = 0,
    @SerialName("page")
    val page: Int = 0,
    @SerialName("per_page")
    val perPage: Int = 0,
    @SerialName("total")
    val total: Int = 0,
    @SerialName("token")
    val token: String = "",
) {
    override fun toString(): String {
        return "ApiResponse(success=$status, message=$message, data=$data)"
    }
}
