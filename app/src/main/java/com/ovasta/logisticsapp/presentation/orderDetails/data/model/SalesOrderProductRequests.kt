package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import com.google.gson.annotations.SerializedName

/** Body of `sales-orders/{id}/update`: new quantity and unit price for one product. */
data class UpdateSalesOrderProductRequest(
    @SerializedName("catalog_product") val catalogProduct: Boolean,
    @SerializedName("product") val product: ProductUpdate,
) {
    data class ProductUpdate(
        @SerializedName("id") val id: Int,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("price") val price: Int,
    )
}

/** Body of `sales-orders/{id}/delete-product`. */
data class DeleteSalesOrderProductRequest(
    @SerializedName("catalog_product") val catalogProduct: Boolean,
    @SerializedName("product_id") val productId: Int,
)
