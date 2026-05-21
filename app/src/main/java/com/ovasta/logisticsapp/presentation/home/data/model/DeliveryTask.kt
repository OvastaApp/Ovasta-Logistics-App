package com.ovasta.logisticsapp.presentation.home.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
@Keep
data class DeliveryTask(

    @get:PropertyName("order_id")
    @set:PropertyName("order_id")
    var orderId: Int = 0,

    @get:PropertyName("status_id")
    @set:PropertyName("status_id")
    var statusId: Int? = null,

    @get:PropertyName("status_name")
    @set:PropertyName("status_name")
    var statusName: String = "",

    @get:PropertyName("sender_mobile")
    @set:PropertyName("sender_mobile")
    var senderMobile: String = "",

    @get:PropertyName("from_address")
    @set:PropertyName("from_address")
    var fromAddress: String = "",

    @get:PropertyName("to_address")
    @set:PropertyName("to_address")
    var toAddress: String = "",

    @get:PropertyName("receiver_mobile")
    @set:PropertyName("receiver_mobile")
    var receiverMobile: String = "",

    @get:PropertyName("delivery_price")
    @set:PropertyName("delivery_price")
    var deliveryPrice: Int? = null,

    @get:PropertyName("collection_amount")
    @set:PropertyName("collection_amount")
    var collectionAmount: Int? = null,

    @get:PropertyName("note")
    @set:PropertyName("note")
    var note: String = "",

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Timestamp? = null,

    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Timestamp? = null

) : Parcelable