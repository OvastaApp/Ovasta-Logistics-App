package com.ovasta.logisticsapp.presentation.home.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A delivery order waiting for a courier (districts/{id}/delivery_orders/{orderId}). */
@Serializable
data class DeliveryTask(
    @SerialName("order_id")
    var orderId: Int = 0,
    @SerialName("status_id")
    var statusId: Int? = null,
    @SerialName("status_name")
    var statusName: String = "",
    @SerialName("sender_mobile")
    var senderMobile: String = "",
    @SerialName("from_address")
    var fromAddress: String = "",
    @SerialName("to_address")
    var toAddress: String = "",
    @SerialName("receiver_mobile")
    var receiverMobile: String = "",
    @SerialName("delivery_price")
    var deliveryPrice: Double? = null,
    @SerialName("collection_amount")
    var collectionAmount: Double? = null,
    @SerialName("note")
    var note: String = "",
    @SerialName("created_at")
    var createdAt: Timestamp? = null,
    @SerialName("updated_at")
    var updatedAt: Timestamp? = null
)
