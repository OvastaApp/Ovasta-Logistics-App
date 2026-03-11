package com.ovasta.logisticsapp.presentation.home.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.memoryCacheSettings
import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.FirebaseConstants
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DRIVERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ONLINE_DRIVERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_WORKERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest

class HomeRemoteDataSource(
    private val db: FirebaseFirestore,
    private val homeApi: HomeApi
) : IHomeRemoteDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAssignedTasks(
        userId: Int, districId: Int, userType: String
    ): Flow<List<HomeTask>> = callbackFlow {
        val listenerRegistration =
            db.collection(FIRESTORE_ROOT_WORKERS_NAME).document(districId.toString())
                .collection(userType).document(userId.toString())
                .collection(FirebaseConstants.FIRESTORE_WORKER_ORDERS_NAME)
                .addSnapshotListener { value, error ->
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
        listenToOrdersChanges(orderIds)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenToOrdersChanges(
        orderIds: List<String>
    ): Flow<List<HomeTask>> = callbackFlow {

        if (orderIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ordersCollection = db.collection(FIRESTORE_ROOT_ORDERS_NAME)

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

    override suspend fun logLocation(
        userId: Int,
        districtId: Int,
        latitude: Double,
        longitude: Double
    ) {
        val locationData = hashMapOf(
            "userId" to userId,
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection(FIRESTORE_ROOT_WORKERS_NAME)
            .document(FIRESTORE_ROOT_DISTRICT_NAME)
            .collection(districtId.toString())
            .document(FIRESTORE_ROOT_DRIVERS_NAME)
            .collection(FIRESTORE_ROOT_ONLINE_DRIVERS_NAME)
            .document(userId.toString())
            .set(locationData, SetOptions.merge())
            .addOnFailureListener {
                Log.e("logLocation", "Failed to update location", it)
            }
    }

    override suspend fun changePartnerStatus(isOnline: Boolean?) =
        homeApi.changePartnerStatus(isOnline)

    override suspend fun getPartnerStatus() = homeApi.getPartnerStatus()
}