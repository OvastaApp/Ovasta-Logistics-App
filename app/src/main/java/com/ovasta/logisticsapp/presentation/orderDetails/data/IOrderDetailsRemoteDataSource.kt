package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import kotlinx.coroutines.flow.Flow

interface IOrderDetailsRemoteDataSource {
    suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask>

    /**
     * Persists edits to each product's document in the order's `products` subcollection,
     * matching documents by [FirebaseProduct.productId].
     */
    suspend fun updateProducts(
        districtId: Int,
        taskId: Int,
        products: List<FirebaseProduct>
    )

    /**
     * Updates the order document's status id/name (e.g. assigned -> on the way to client).
     *
     * @param receivedAmount cash collected from the client; persisted alongside the status when
     * the order is delivered, omitted from the update otherwise.
     */
    suspend fun updateOrderStatus(
        districtId: Int,
        taskId: Int,
        statusId: Int,
        statusName: String,
        receivedAmount: Double? = null
    )

    /** Fetches the sources the given product can be assigned to. */
    suspend fun getProductSources(productId: Int): List<ProductSource>
}
