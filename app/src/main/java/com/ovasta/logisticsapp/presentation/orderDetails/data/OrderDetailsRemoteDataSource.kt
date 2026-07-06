package com.ovasta.logisticsapp.presentation.orderDetails.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_PRODUCTS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderDetailsRemoteDataSource(
    private val db: FirebaseFirestore,
    private val api: OrderDetailsApi,
) : IOrderDetailsRemoteDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask> = callbackFlow {

        val orderDocument =
            db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
                .collection(FIRESTORE_ROOT_ORDERS_NAME).document(taskId.toString())

        // Products live in a `products` subcollection of the order document rather than
        // as an inline array field, so they need their own listener merged into the task.
        var latestTask: HomeTask? = null
        var latestProducts: List<FirebaseProduct>? = null

        fun emitIfReady() {
            val task = latestTask ?: return
            trySend(task.copy(products = latestProducts.orEmpty()))
        }

        val taskRegistration = orderDocument.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("orderDocListener", "Error", error)
                return@addSnapshotListener
            }
            snapshot?.toObject(HomeTask::class.java)?.let { task ->
                latestTask = task
                emitIfReady()
            }
        }

        val productsRegistration =
            orderDocument.collection(FIRESTORE_PRODUCTS_NAME)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("orderProductsListener", "Error", error)
                        return@addSnapshotListener
                    }
                    latestProducts = snapshot?.documents?.mapNotNull {
                        it.toObject(FirebaseProduct::class.java)
                    }.orEmpty()
                    emitIfReady()
                }

        awaitClose {
            taskRegistration.remove()
            productsRegistration.remove()
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
                mapOf(
                    "item_price" to product.itemPrice,
                    "quantity" to product.quantity,
                    "picked_quantity" to product.pickedQuantity,
                    "source" to product.source,
                    "total_price" to product.totalPrice,
                    "updated_at" to product.updatedAt,
                )
            )
        }
        batch.commit().await()
    }

    override suspend fun updateOrderStatus(
        districtId: Int,
        taskId: Int,
        statusId: Int,
        statusName: String,
        receivedAmount: Double?
    ) {
        val fields = buildMap {
            put("status_id", statusId)
            put("status_name", statusName)
            put("updated_at", FieldValue.serverTimestamp())
            receivedAmount?.let { put("received_amount", it) }
        }
        db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
            .collection(FIRESTORE_ROOT_ORDERS_NAME).document(taskId.toString())
            .update(fields).await()
    }

    override suspend fun getProductSources(productId: Int): List<ProductSource> =
        api.getProductSources(productId).data.orEmpty()
}
