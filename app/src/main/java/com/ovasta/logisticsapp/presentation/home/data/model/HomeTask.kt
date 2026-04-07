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
data class HomeTask(
    @get:PropertyName("order_id")
    @PropertyName("order_id")
    val taskId: Int = 0,

    @get:PropertyName("customer_lat")
    @PropertyName("customer_lat")
    val clientLat: Double = 0.0,

    @get:PropertyName("customer_long")
    @PropertyName("customer_long")
    val clientLang: Double = 0.0,

    @get:PropertyName("customer_name")
    @PropertyName("customer_name")
    val customerName: String? = null,

    @get:PropertyName("customer_address")
    @PropertyName("customer_address")
    val customerAddress: String? = null,

    @get:PropertyName("customer_mobile")
    @PropertyName("customer_mobile")
    val clientPhone: String? = null,

    @get:PropertyName("customer_whatsapp")
    @PropertyName("customer_whatsapp")
    val clientWhatsapp: String? = null,

    @get:PropertyName("delivery_fees")
    @PropertyName("delivery_fees")
    val deliveryFees: Float = 0f,

    @get:PropertyName("total_price")
    @PropertyName("total_price")
    val totalPrice: Float = 0f,

    @get:PropertyName("items_count")
    @PropertyName("items_count")
    val itemsCount: Int = 0,

    @get:PropertyName("status_id")
    @PropertyName("status_id")
    val statusId: Int = 0,
    @get:PropertyName("status_name")
    @PropertyName("status_name")
    val statusName: String = "",

    @get:PropertyName("products")
    @PropertyName("products")
    var products: List<FirebaseProduct> = emptyList(),

    @get:PropertyName("updated_at")
    @PropertyName("updated_at")
    val updatedAt: Timestamp? = null
) : Parcelable


@Parcelize
@Keep
data class FirebaseProduct(
    @get:PropertyName("catalog_product")
    @PropertyName("catalog_product")
    val catalogProduct: Boolean? = null,

    @get:PropertyName("is_stock")
    @PropertyName("is_stock")
    val isStock: Boolean? = null,

    @get:PropertyName("item_price")
    @PropertyName("item_price")
    val itemPrice: Int? = null,
    @get:PropertyName("main_sys_id")
    @PropertyName("main_sys_id")
    val mainSysId: Int? = null,

    @get:PropertyName("name")
    @PropertyName("name")
    val name: String? = null,

    @get:PropertyName("quantity")
    @PropertyName("quantity")
    val quantity: Int? = null,

    @get:PropertyName("source")
    @PropertyName("source")
    val source: String? = null,

    @get:PropertyName("image_url")
    @PropertyName("source")
    val imageUrl: String? = null,

    @get:PropertyName("total_price")
    @PropertyName("total_price")
    val totalPrice: Double? = null,
    @get:PropertyName("updated_at")
    @PropertyName("updated_at")
    val updatedAt: Timestamp? = null
) : Parcelable