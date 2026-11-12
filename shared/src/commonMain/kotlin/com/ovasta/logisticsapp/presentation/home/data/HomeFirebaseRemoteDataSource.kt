package com.ovasta.logisticsapp.presentation.home.data

import com.ovasta.logisticsapp.base.UserType
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DELIVERY_ORDERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_DISTRICT_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ONLINE_DRIVERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_ORDERS_NAME
import com.ovasta.logisticsapp.data.FirebaseConstants.FIRESTORE_ROOT_PICKERS_NAME
import com.ovasta.logisticsapp.data.remote.hasNonNull
import com.ovasta.logisticsapp.presentation.home.data.model.DeliveryTask
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

class HomeFirebaseRemoteDataSource(
    private val db: FirebaseFirestore
) : IHomeFirebaseRemoteDataSource {

    override suspend fun logLocation(
        userId: Int, districtId: Int, latitude: Double, longitude: Double
    ) {
        db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
            .collection(FIRESTORE_ROOT_ONLINE_DRIVERS_NAME).document(userId.toString())
            .set(DriverLocation(userId, latitude, longitude), merge = true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAssignedOrders(
        userId: Int,
        userType: Int,
        districtId: Int
    ): Flow<List<HomeTask>> =
        db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
            .collection(
                if (userType == UserType.COURIER.typeId)
                    FIRESTORE_ROOT_ONLINE_DRIVERS_NAME
                else
                    FIRESTORE_ROOT_PICKERS_NAME
            ).document(userId.toString())
            .collection(FIRESTORE_ROOT_ORDERS_NAME)
            .snapshots
            .map { snapshot ->
                snapshot.documents.associate { doc ->
                    doc.id to runCatching { doc.get<Timestamp?>("assigned_at") }.getOrNull()
                }
            }
            .flatMapLatest { assignedAtByOrderId ->
                listenToOrdersChanges(assignedAtByOrderId, districtId)
            }

    /**
     * Listens to each assigned order's document and emits all orders read so far whenever one
     * changes, in the order they first arrived.
     */
    private fun listenToOrdersChanges(
        assignedAtByOrderId: Map<String, Timestamp?>, districtId: Int
    ): Flow<List<HomeTask>> {
        if (assignedAtByOrderId.isEmpty()) return flowOf(emptyList())

        val ordersCollection =
            db.collection(FIRESTORE_ROOT_DISTRICT_NAME).document(districtId.toString())
                .collection(FIRESTORE_ROOT_ORDERS_NAME)

        return channelFlow {
            val tasksMap = mutableMapOf<String, HomeTask>()
            val mutex = Mutex()
            assignedAtByOrderId.forEach { (orderId, assignedAt) ->
                launch {
                    ordersCollection.document(orderId).snapshots().collect { snapshot ->
                        if (!snapshot.exists) return@collect
                        // Skip a document that can't be read rather than failing every order
                        val task = runCatching { snapshot.data<HomeTask>() }.getOrNull()
                            ?: return@collect
                        val tasks = mutex.withLock {
                            tasksMap[orderId] = task.copy(
                                assignedAt = assignedAt ?: task.assignedAt,
                                isReceived = snapshot.hasNonNull("received_at")
                            )
                            tasksMap.values.toList()
                        }
                        send(tasks)
                    }
                }
            }
        }
    }

    override suspend fun listenToNewDeliveryTasks(
        userId: Int,
        districtId: Int
    ): Flow<List<DeliveryTask>> = db
        .collection(FIRESTORE_ROOT_DISTRICT_NAME)
        .document(districtId.toString())
        .collection(FIRESTORE_ROOT_DELIVERY_ORDERS_NAME)
        .snapshots
        .map { snapshot ->
            snapshot.documents.mapNotNull { document ->
                runCatching {
                    document.data<DeliveryTask>().copy(orderId = document.id.toIntOrNull() ?: 0)
                }.getOrNull()
            }
        }

    /** The driver's latest position in online_drivers/{id}; timestamp is set by the server. */
    @Serializable
    private class DriverLocation(
        val userId: Int,
        val latitude: Double,
        val longitude: Double,
        val timestamp: BaseTimestamp = Timestamp.ServerTimestamp,
    )
}
