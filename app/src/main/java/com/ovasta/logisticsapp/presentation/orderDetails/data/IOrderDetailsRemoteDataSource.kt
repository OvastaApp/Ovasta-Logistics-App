package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
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
}
