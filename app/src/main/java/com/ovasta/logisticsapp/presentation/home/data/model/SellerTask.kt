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
data class SellerTask(
    @get:PropertyName("order_id")
    @PropertyName("order_id")
    val orderId: Int,

    @get:PropertyName("status_id")
    @PropertyName("status_id")
    val statusId: Int? = null,

    @get:PropertyName("status_name")
    @PropertyName("status_name")
    val statusName: String = "",

    @get:PropertyName("sender_mobile")
    @PropertyName("sender_mobile")
    val senderMobile: String = "",

    @get:PropertyName("from_address")
    @PropertyName("from_address")
    val fromAddress: String = "",

    @get:PropertyName("to_address")
    @PropertyName("to_address")
    val toAddress: String = "",

    @get:PropertyName("receiver_mobile")
    @PropertyName("receiver_mobile")
    val receiverMobile: String = "",

    @get:PropertyName("delivery_price")
    @PropertyName("delivery_price")
    val deliveryPrice: Int? = null,

    @get:PropertyName("collection_amount")
    @PropertyName("collection_amount")
    val collectionAmount: Int? = null,

    @get:PropertyName("note")
    @PropertyName("note")
    val note: String = "",

    @get:PropertyName("created_at")
    @PropertyName("created_at")
    val createdAt: String = "",

    @get:PropertyName("updated_at")
    @PropertyName("updated_at")
    val updatedAt: String = ""

) : Parcelable