package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import kotlinx.coroutines.flow.Flow

class OrderDetailsRepository(
    private val taskDetailsRemoteDataSource: IOrderDetailsRemoteDataSource,
) : IOrderDetailsRepository {
    override suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask> = taskDetailsRemoteDataSource.listenToOrderChanges(districtId, taskId)

    override suspend fun updateProducts(
        districtId: Int,
        taskId: Int,
        products: List<FirebaseProduct>
    ) = taskDetailsRemoteDataSource.updateProducts(districtId, taskId, products)

    override suspend fun updateOrderStatus(
        districtId: Int,
        taskId: Int,
        statusId: Int,
        statusName: String,
        receivedAmount: Double?
    ) = taskDetailsRemoteDataSource.updateOrderStatus(
        districtId, taskId, statusId, statusName, receivedAmount
    )

    override suspend fun getProductSources(productId: Int): List<ProductSource> =
        taskDetailsRemoteDataSource.getProductSources(productId)
}
