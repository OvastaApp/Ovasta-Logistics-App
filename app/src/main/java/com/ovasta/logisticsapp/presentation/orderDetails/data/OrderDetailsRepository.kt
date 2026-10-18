package com.ovasta.logisticsapp.presentation.orderDetails.data

import android.net.Uri
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

    override suspend fun deliverSalesOrder(orderId: Int, collectedAmount: Double) =
        taskDetailsRemoteDataSource.deliverSalesOrder(orderId, collectedAmount)

    override suspend fun updateSalesOrderProduct(
        orderId: Int,
        catalogProduct: Boolean,
        productId: Int,
        quantity: Int,
        price: Int
    ) = taskDetailsRemoteDataSource.updateSalesOrderProduct(
        orderId, catalogProduct, productId, quantity, price
    )

    override suspend fun deleteSalesOrderProduct(
        orderId: Int,
        catalogProduct: Boolean,
        productId: Int
    ) = taskDetailsRemoteDataSource.deleteSalesOrderProduct(orderId, catalogProduct, productId)

    override suspend fun requestOrderEdit(orderId: Int, image: Uri) =
        taskDetailsRemoteDataSource.requestOrderEdit(orderId, image)

    override suspend fun uploadOrderImage(orderId: Int, image: Uri) =
        taskDetailsRemoteDataSource.uploadOrderImage(orderId, image)
}
