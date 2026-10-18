package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import com.google.gson.annotations.SerializedName

/** Body of `sales-orders/{id}/update`: new quantity and unit price for one product. */
data class UpdateSalesOrderProductRequest(
    @SerializedName("catalog_product") val catalogProduct: Boolean,
    @SerializedName("product") val product: ProductUpdate,
) {
    /** [quantity] and [price] can be fractional, e.g. 0.25 kg at 20.25. */
    data class ProductUpdate(
        @SerializedName("id") val id: Int,
        @SerializedName("quantity") val quantity: Double,
        @SerializedName("price") val price: Double,
    )
}

/** Body of `sales-orders/{id}/delete-product`. */
data class DeleteSalesOrderProductRequest(
    @SerializedName("catalog_product") val catalogProduct: Boolean,
    @SerializedName("product_id") val productId: Int,
)
