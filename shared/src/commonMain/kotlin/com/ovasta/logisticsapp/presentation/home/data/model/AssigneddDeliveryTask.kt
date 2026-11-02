package com.ovasta.logisticsapp.presentation.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// All fields are nullable: the API returns null for some of them
@Serializable
data class AssignedDeliveryTask(

    @SerialName("order_id")
    var orderId: Int? = null,

    @SerialName("status_id")
    var statusId: Int? = null,

    @SerialName("status_name")
    var statusName: String? = null,

    @SerialName("sender_mobile")
    var senderMobile: String? = null,

    @SerialName("from_address")
    var fromAddress: String? = null,

    @SerialName("to_address")
    var toAddress: String? = null,

    @SerialName("receiver_mobile")
    var receiverMobile: String? = null,

    @SerialName("delivery_price")
    var deliveryPrice: Double? = null,

    @SerialName("collection_amount")
    var collectionAmount: Double? = null,

    @SerialName("note")
    var note: String? = null,

    @SerialName("created_at")
    var createdAt: String? = null,

    @SerialName("updated_at")
    var updatedAt: String? = null,

    @SerialName("assigned_at")
    var assignedAt: String? = null

)