package com.ovasta.logisticsapp.presentation.home.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_COURIER_ID_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DELIVERY_ORDERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ONLINE_DRIVERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest

class HomeFirebaseRemoteDataSource(
    private val db: FirebaseFirestore
) : IHomeFirebaseRemoteDataSource {

    override suspend fun logLocation(
        userId: Int, districtId: Int, latitude: Double, longitude: Double
    ) {
        val locationData = hashMapOf(
            "userId" to userId,
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
            .collection(FIRESTORE_ROOT_ONLINE_DRIVERS_NAME).document(userId.toString())
            .set(locationData, SetOptions.merge()).addOnFailureListener {
                Log.e("logLocation", "Failed to update location", it)
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAssignedOrders(userId: Int, districtId: Int): Flow<List<HomeTask>> =
        callbackFlow {
            val listenerRegistration =
                db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
                    .collection(FIRESTORE_ROOT_ONLINE_DRIVERS_NAME).document(userId.toString())
                    .collection(FIRESTORE_ROOT_ORDERS_NAME).addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e("assignedOrders", "Error fetching orders", error)
                            close(error)
                            return@addSnapshotListener
                        }

                        val orderIds = value?.documents?.map { it.id } ?: emptyList()
                        Log.d("assignedOrderIds", "$orderIds")
                        trySend(orderIds)
                    }

            awaitClose { listenerRegistration.remove() }
        }.flatMapLatest { orderIds ->
            listenToOrdersChanges(orderIds, districtId)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenToOrdersChanges(
        orderIds: List<String>, districtId: Int
    ): Flow<List<HomeTask>> = callbackFlow {

        if (orderIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ordersCollection =
            db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
                .collection(FIRESTORE_ROOT_ORDERS_NAME)


        val tasksMap = mutableMapOf<String, HomeTask>()
        val listeners = mutableListOf<ListenerRegistration>()

        orderIds.forEach { orderId ->
            val registration =
                ordersCollection.document(orderId).addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("orderDocListener", "Error", error)
                        return@addSnapshotListener
                    }
                    snapshot?.toObject(HomeTask::class.java)?.let { task ->
                        tasksMap[orderId] = task
                        trySend(tasksMap.values.toList())
                    }
                }
            listeners.add(registration)
        }
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun listenToNewDeliveryTasks(
        userId: Int,
        districtId: Int
    ): Flow<List<DeliveryTask>> = callbackFlow {

        val listenerRegistration = db
            .collection(FIRESTORE_ROOT_DISTRICT_NAME)
            .document(districtId.toString())
            .collection(FIRESTORE_ROOT_DELIVERY_ORDERS_NAME)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    Log.e("assignedOrders", "Error fetching orders", error)
                    close(error)
                    return@addSnapshotListener
                }

                val orders = value?.documents?.mapNotNull { document ->
                    Log.d("sda", "${document.id.toIntOrNull() ?: 0}")
                    try {
                        document.toObject(DeliveryTask::class.java)?.copy(
                            orderId = document.id.toIntOrNull() ?: 0
                        )
                    } catch (e: Exception) {
                        Log.d("Exceptionss", "Error parsing document ${document.id}: ${e.message}")
                        null
                    }

                } ?: emptyList()

                trySend(orders)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }
}