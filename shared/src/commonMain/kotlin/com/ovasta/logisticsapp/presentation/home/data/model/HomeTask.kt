package com.ovasta.logisticsapp.presentation.home.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// Firestore documents (districts/{id}/orders/{orderId}), decoded by GitLive. Every field has a
// default so a missing field falls back to it; unknown fields are ignored.

@Serializable
data class HomeTask(
    @SerialName("order_id")
    val taskId: Int = 0,
    @SerialName("customer_lat")
    val clientLat: Double = 0.0,
    @SerialName("customer_long")
    val clientLang: Double = 0.0,
    @SerialName("customer_name")
    val customerName: String? = null,
    @SerialName("customer_address")
    val customerAddress: String? = null,
    @SerialName("customer_mobile")
    val clientPhone: String? = null,
    @SerialName("customer_whatsapp")
    val clientWhatsapp: String? = null,
    @SerialName("delivery_fees")
    val deliveryFees: Float = 0f,
    @SerialName("total_price")
    val totalPrice: Float = 0f,
    @SerialName("items_count")
    val itemsCount: Int = 0,
    @SerialName("status_id")
    val statusId: Int = 0,
    @SerialName("status_name")
    val statusName: String = "",
    @SerialName("products")
    var products: List<FirebaseProduct> = emptyList(),
    @SerialName("updated_at")
    val updatedAt: Timestamp? = null,
    // Filled from the agent's assigned-order doc (online_drivers/{id}/orders/{orderId})
    @SerialName("assigned_at")
    val assignedAt: Timestamp? = null,
    /** Set by the backend once an edit request is approved: the agent may now edit the order. */
    @SerialName("is_edit_allowed")
    val isEditable: Boolean = false,
    /**
     * Whether the order's `received_at` is set, i.e. the agent already confirmed receiving it
     * (`sales-orders/{id}/confirm-receive`). Filled in by the data source rather than decoded,
     * so it works whatever type the backend stores `received_at` as.
     */
    @Transient
    val isReceived: Boolean = false
)

@Serializable
data class FirebaseProduct(
    @SerialName("catalog_product")
    val catalogProduct: Boolean? = null,
    @SerialName("is_stock")
    val isStock: Boolean? = null,
    // Decimal: unit price can have piasters (e.g. 20.25)
    @SerialName("item_price")
    val itemPrice: Double? = null,
    @SerialName("main_sys_id")
    val mainSysId: Int? = null,
    @SerialName("product_id")
    val productId: Int? = null,
    @SerialName("name")
    val name: String? = null,
    // Decimal: products sold by weight can have fractional quantities (e.g. 0.5, 0.250)
    @SerialName("quantity")
    val quantity: Double? = null,
    @SerialName("picked_quantity")
    val pickedQuantity: Double? = null,
    @SerialName("source")
    val source: String? = null,
    @SerialName("image")
    val imageUrl: String? = null,
    @SerialName("total_price")
    val totalPrice: Double? = null,
    @SerialName("updated_at")
    val updatedAt: Timestamp? = null
)
