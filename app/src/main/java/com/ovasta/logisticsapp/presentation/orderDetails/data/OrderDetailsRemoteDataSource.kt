package com.ovasta.logisticsapp.presentation.orderDetails.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OrderDetailsRemoteDataSource(
    private val db: FirebaseFirestore
) : IOrderDetailsRemoteDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask> = callbackFlow {

        val ordersCollection =
            db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
                .collection(FIRESTORE_ROOT_ORDERS_NAME)

        val registration =
            ordersCollection.document(taskId.toString()).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("orderDocListener", "Error", error)
                    return@addSnapshotListener
                }
                snapshot?.toObject(HomeTask::class.java)?.let { task ->
                    trySend(task)
                }
            }

        awaitClose {
            registration.remove()
        }
    }
}
