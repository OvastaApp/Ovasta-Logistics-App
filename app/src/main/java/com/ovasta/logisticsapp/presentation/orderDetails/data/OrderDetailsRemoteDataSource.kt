package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.google.firebase.firestore.FirebaseFirestore
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_PRODUCTS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class OrderDetailsRemoteDataSource : IOrderDetailsRemoteDataSource {

    override suspend fun getTaskDetails(
        branchId: Int,
        taskId: Int
    ): Flow<HomeTask> = flow {

        val firestore = FirebaseFirestore.getInstance()
        val ordersCollection = firestore.collection(FIRESTORE_ROOT_ORDERS_NAME)

        val taskDocRef = ordersCollection.document(taskId.toString())
        val taskSnapshot = taskDocRef.get().await()

        if (!taskSnapshot.exists()) {
            return@flow
        }

        val task = taskSnapshot.toObject(HomeTask::class.java)
            ?: return@flow

        val productsSnapshot = taskDocRef
            .collection(FIRESTORE_PRODUCTS_NAME)
            .get()
            .await()

        task.products = productsSnapshot.toObjects(FirebaseProduct::class.java)

        emit(task)
    }
}
