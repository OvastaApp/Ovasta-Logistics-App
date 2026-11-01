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
// All fields are nullable: Gson bypasses Kotlin defaults and writes null when the API returns null
data class AssignedDeliveryTask(

    @SerializedName("order_id")
    var orderId: Int? = null,

    @SerializedName("status_id")
    var statusId: Int? = null,

    @SerializedName("status_name")
    var statusName: String? = null,

    @SerializedName("sender_mobile")
    var senderMobile: String? = null,

    @SerializedName("from_address")
    var fromAddress: String? = null,

    @SerializedName("to_address")
    var toAddress: String? = null,

    @SerializedName("receiver_mobile")
    var receiverMobile: String? = null,

    @SerializedName("delivery_price")
    var deliveryPrice: Double? = null,

    @SerializedName("collection_amount")
    var collectionAmount: Double? = null,

    @SerializedName("note")
    var note: String? = null,

    @SerializedName("created_at")
    var createdAt: String? = null,

    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @SerializedName("assigned_at")
    var assignedAt: String? = null

) : Parcelable