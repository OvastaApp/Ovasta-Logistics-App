package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_PRODUCTS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DeleteSalesOrderProductRequest
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DeliverSalesOrderRequest
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.UpdateSalesOrderProductRequest
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart

class OrderDetailsRemoteDataSource(
    private val db: FirebaseFirestore,
    private val api: OrderDetailsApi,
) : IOrderDetailsRemoteDataSource {

    override suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask> {
        val orderDocument =
            db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
                .collection(FIRESTORE_ROOT_ORDERS_NAME).document(taskId.toString())

        // A listener error stops updates quietly, as before
        val task = orderDocument.snapshots()
            .mapNotNull { snapshot ->
                if (!snapshot.exists) null else runCatching { snapshot.data<HomeTask>() }.getOrNull()
            }
            .catch { }

        // Products live in a `products` subcollection of the order document rather than
        // as an inline array field, so they need their own listener merged into the task.
        val products = orderDocument.collection(FIRESTORE_PRODUCTS_NAME).snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { runCatching { it.data<FirebaseProduct>() }.getOrNull() }
            }
            .catch { }
            .onStart { emit(emptyList()) }

        return combine(task, products) { latestTask, latestProducts ->
            latestTask.copy(products = latestProducts)
        }
    }

    override suspend fun updateProducts(
        districtId: Int,
        taskId: Int,
        products: List<FirebaseProduct>
    ) {
        val productsCollection =
            db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
                .collection(FIRESTORE_ROOT_ORDERS_NAME).document(taskId.toString())
                .collection(FIRESTORE_PRODUCTS_NAME)

        val batch = db.batch()
        products.forEach { product ->
            val productId = product.productId ?: return@forEach
            batch.update(
                productsCollection.document(productId.toString()),
                "item_price" to product.itemPrice,
                "quantity" to product.quantity,
                "picked_quantity" to product.pickedQuantity,
                "source" to product.source,
                "total_price" to product.totalPrice,
                "updated_at" to product.updatedAt,
            )
        }
        batch.commit()
    }

    override suspend fun updateOrderStatus(
        districtId: Int,
        taskId: Int,
        statusId: Int,
        statusName: String,
        receivedAmount: Double?
    ) {
        val fields = buildList {
            add("status_id" to statusId)
            add("status_name" to statusName)
            add("updated_at" to FieldValue.serverTimestamp)
            receivedAmount?.let { add("received_amount" to it) }
        }
        db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
            .collection(FIRESTORE_ROOT_ORDERS_NAME).document(taskId.toString())
            .update(*fields.toTypedArray())
    }

    override suspend fun getProductSources(productId: Int): List<ProductSource> =
        api.getProductSources(productId).data.orEmpty()

    override suspend fun deliverSalesOrder(orderId: Int, collectedAmount: Double) {
        api.deliverSalesOrder(orderId, DeliverSalesOrderRequest(collectedAmount))
    }

    override suspend fun updateSalesOrderProduct(
        orderId: Int,
        catalogProduct: Boolean,
        productId: Int,
        quantity: Double,
        price: Double
    ) {
        api.updateSalesOrderProduct(
            orderId,
            UpdateSalesOrderProductRequest(
                catalogProduct = catalogProduct,
                product = UpdateSalesOrderProductRequest.ProductUpdate(productId, quantity, price)
            )
        )
    }

    override suspend fun deleteSalesOrderProduct(
        orderId: Int,
        catalogProduct: Boolean,
        productId: Int
    ) {
        api.deleteSalesOrderProduct(orderId, DeleteSalesOrderProductRequest(catalogProduct, productId))
    }

    override suspend fun requestOrderEdit(orderId: Int, image: ByteArray) {
        api.requestOrderEdit(orderId, image)
    }

    override suspend fun uploadOrderImage(orderId: Int, image: ByteArray) {
        api.uploadOrderImage(orderId, image)
    }
}
