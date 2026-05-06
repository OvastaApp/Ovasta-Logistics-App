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
    val orderId: Int = 0,

    @get:PropertyName("seller_id")
    @PropertyName("seller_id")
    val sellerId: Int = 0,
    @get:PropertyName("seller_name")
    @PropertyName("seller_name")
    val sellerName: String = "",

    @get:PropertyName("courier_id")
    @PropertyName("courier_id")
    val courierId: Int = 0,

    @get:PropertyName("customer_address")
    @PropertyName("customer_address")
    val customerAddress: String? = null,

    @get:PropertyName("customer_mobile")
    @PropertyName("customer_mobile")
    val clientPhone: String? = null,

    @get:PropertyName("seller_mobile")
    @PropertyName("seller_mobile")
    val sellerMobile: String? = null,

    @get:PropertyName("delivery_fees")
    @PropertyName("delivery_fees")
    val deliveryFees: Double = 00.0,

    @get:PropertyName("notes")
    @PropertyName("notes")
    val notes: String? = null,

    @get:PropertyName("status_id")
    @PropertyName("status_id")
    val statusId: Int = 0,
    
    @get:PropertyName("customer_lat")
    @PropertyName("customer_lat")
    val clientLat: Double = 0.0,

    @get:PropertyName("customer_long")
    @PropertyName("customer_long")
    val clientLang: Double = 0.0,

    @get:PropertyName("customer_name")
    @PropertyName("customer_name")
    val customerName: String? = null,


    @get:PropertyName("total_price")
    @PropertyName("total_price")
    val totalPrice: Float = 0f,

    @get:PropertyName("customer_whatsapp")
    @PropertyName("customer_whatsapp")
    val clientWhatsapp: String? = null,


    @get:PropertyName("items_count")
    @PropertyName("items_count")
    val itemsCount: Int = 0,


    @get:PropertyName("status_name")
    @PropertyName("status_name")
    val statusName: String = "",

    @get:PropertyName("products")
    @PropertyName("products")
    var products: List<FirebaseProduct> = emptyList(),

    @get:PropertyName("created_at")
    @PropertyName("created_at")
    val createdAt: Timestamp? = null,

    @get:PropertyName("updated_at")
    @PropertyName("updated_at")
    val updatedAt: Timestamp? = null
) : Parcelable