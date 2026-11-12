package com.ovasta.logisticsapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One page of a paginated list, as sent inside [ApiResponse.data] by paginated endpoints. */
@Serializable
data class PaginatedData<T>(
    @SerialName("current_page") val currentPage: Int? = null,
    @SerialName("data") val data: List<T>? = null,
    @SerialName("last_page") val lastPage: Int? = null,
    @SerialName("per_page") val perPage: Int? = null,
    @SerialName("total") val total: Int? = null,
) {
    val hasNextPage: Boolean get() = (currentPage ?: 0) < (lastPage ?: 0)
}
