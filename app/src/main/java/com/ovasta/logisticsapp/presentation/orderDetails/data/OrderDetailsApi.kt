package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderDetailsApi {
    /** Returns the sources the given product can be assigned to. */
    @GET("products/{id}/sources")
    suspend fun getProductSources(
        @Path("id") productId: Int
    ): ApiResponse<List<ProductSource>>
}
