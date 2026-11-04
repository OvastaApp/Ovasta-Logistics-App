package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body of `sales-orders/{id}/update`: new quantity and unit price for one product. */
@Serializable
data class UpdateSalesOrderProductRequest(
    @SerialName("catalog_product") val catalogProduct: Boolean,
    @SerialName("product") val product: ProductUpdate,
) {
    /** [quantity] and [price] can be fractional, e.g. 0.25 kg at 20.25. */
    @Serializable
    data class ProductUpdate(
        @SerialName("id") val id: Int,
        @SerialName("quantity") val quantity: Double,
        @SerialName("price") val price: Double,
    )
}

/** Body of `sales-orders/{id}/delete-product`. */
@Serializable
data class DeleteSalesOrderProductRequest(
    @SerialName("catalog_product") val catalogProduct: Boolean,
    @SerialName("product_id") val productId: Int,
)
