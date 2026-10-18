package com.ovasta.logisticsapp.presentation.orderDetails.data

import android.net.Uri
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import kotlinx.coroutines.flow.Flow

interface IOrderDetailsRepository {
    suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask>

    suspend fun updateProducts(
        districtId: Int,
        taskId: Int,
        products: List<FirebaseProduct>
    )

    suspend fun updateOrderStatus(
        districtId: Int,
        taskId: Int,
        statusId: Int,
        statusName: String,
        receivedAmount: Double? = null
    )

    suspend fun getProductSources(productId: Int): List<ProductSource>

    suspend fun deliverSalesOrder(orderId: Int, collectedAmount: Double)

    suspend fun updateSalesOrderProduct(
        orderId: Int,
        catalogProduct: Boolean,
        productId: Int,
        quantity: Double,
        price: Double
    )

    suspend fun deleteSalesOrderProduct(orderId: Int, catalogProduct: Boolean, productId: Int)

    suspend fun requestOrderEdit(orderId: Int, image: Uri)

    suspend fun uploadOrderImage(orderId: Int, image: Uri)
}
