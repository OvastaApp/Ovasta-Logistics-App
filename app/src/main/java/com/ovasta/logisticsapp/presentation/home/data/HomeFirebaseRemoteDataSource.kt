package com.ovasta.logisticsapp.presentation.home.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_COURIER_ID_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ONLINE_DRIVERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_SELLERS_ORDERS_NAME
import com.ovasta.logisticsapp.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.data.model.SellerTask
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
    override suspend fun getAssignedTasks(userId: Int, districtId: Int): Flow<List<HomeTask>> =
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
    override suspend fun getAvailableSellerOrders(
        userId: Int, districtId: Int
    ): Flow<List<SellerTask>> {

        val collection = db.collection(FIRESTORE_ROOT_DISTRICT_NAME)
            .document(districtId.toString())
            .collection(FIRESTORE_ROOT_SELLERS_ORDERS_NAME)

        val myOrdersQuery = collection.whereEqualTo(FIRESTORE_COURIER_ID_NAME, userId)
        val nullCourierQuery = collection.whereEqualTo(FIRESTORE_COURIER_ID_NAME, null)
        val emptyCourierQuery = collection.whereEqualTo(FIRESTORE_COURIER_ID_NAME, 0)

        // First: emit the merged set of matching document IDs from the 3 queries
        val idsFlow: Flow<Set<String>> = callbackFlow {
            val queryOwnedIds = mutableMapOf<Int, Set<String>>()
            val listeners = mutableListOf<ListenerRegistration>()

            listOf(myOrdersQuery, nullCourierQuery, emptyCourierQuery)
                .forEachIndexed { index, query ->
                    val registration = query.addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e("sellerOrders", "Error fetching seller order ids", error)
                            close(error)
                            return@addSnapshotListener
                        }
                        val currentIds =
                            value?.documents?.map { it.id }?.toSet() ?: emptySet()
                        queryOwnedIds[index] = currentIds

                        val mergedIds = queryOwnedIds.values.flatten().toSet()
                        trySend(mergedIds)
                    }
                    listeners.add(registration)
                }

            awaitClose { listeners.forEach { it.remove() } }
        }

        // Then: switch to per-document listeners so any field change is observed
        return idsFlow.flatMapLatest { ids ->
            listenToSellerOrdersChanges(ids.toList(), collection)
        }
    }

    private fun listenToSellerOrdersChanges(
        orderIds: List<String>,
        collection: com.google.firebase.firestore.CollectionReference
    ): Flow<List<SellerTask>> = callbackFlow {

        if (orderIds.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow awaitClose { }
        }

        val tasksMap = mutableMapOf<String, SellerTask>()
        val listeners = mutableListOf<ListenerRegistration>()

        orderIds.forEach { orderId ->
            val registration = collection.document(orderId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("sellerOrderDocListener", "Error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot == null || !snapshot.exists()) {
                        if (tasksMap.remove(orderId) != null) {
                            trySend(tasksMap.values.toList())
                        }
                        return@addSnapshotListener
                    }
                    snapshot.toObject(SellerTask::class.java)?.let { task ->
                        tasksMap[orderId] = task
                        trySend(tasksMap.values.toList())
                    }
                }
            listeners.add(registration)
        }

        awaitClose { listeners.forEach { it.remove() } }
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

}