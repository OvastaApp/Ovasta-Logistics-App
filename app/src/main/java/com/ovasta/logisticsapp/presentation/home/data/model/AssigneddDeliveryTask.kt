package com.ovasta.logisticsapp.presentation.home.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
@Keep
data class AssignedDeliveryTask(

    @SerializedName("order_id")
    var orderId: Int = 0,

    @SerializedName("status_id")
    var statusId: Int? = null,

    @SerializedName("status_name")
    var statusName: String = "",

    @SerializedName("sender_mobile")
    var senderMobile: String = "",

    @SerializedName("from_address")
    var fromAddress: String = "",

    @SerializedName("to_address")
    var toAddress: String = "",

    @SerializedName("receiver_mobile")
    var receiverMobile: String = "",

    @SerializedName("delivery_price")
    var deliveryPrice: Int? = null,

    @SerializedName("collection_amount")
    var collectionAmount: Int? = null,

    @SerializedName("note")
    var note: String = "",

    @SerializedName("created_at")
    var createdAt: String? = null,

    @SerializedName("updated_at")
    var updatedAt: String? = null

) : Parcelable