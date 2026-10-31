package com.ovasta.logisticsapp.data

import com.google.gson.annotations.SerializedName

/** One page of a paginated list, as sent inside [ApiResponse.data] by paginated endpoints. */
data class PaginatedData<T>(
    @SerializedName("current_page") val currentPage: Int? = null,
    @SerializedName("data") val data: List<T>? = null,
    @SerializedName("last_page") val lastPage: Int? = null,
    @SerializedName("per_page") val perPage: Int? = null,
    @SerializedName("total") val total: Int? = null,
) {
    val hasNextPage: Boolean get() = (currentPage ?: 0) < (lastPage ?: 0)
}
