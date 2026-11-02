package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.data.remote.postJpeg
import com.ovasta.logisticsapp.data.remote.postJson
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DeleteSalesOrderProductRequest
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DeliverSalesOrderRequest
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.UpdateSalesOrderProductRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.JsonElement

class OrderDetailsApi(private val client: HttpClient) {

    /** Returns the sources the given product can be assigned to. */
    suspend fun getProductSources(productId: Int): ApiResponse<List<ProductSource>> =
        client.get("products/$productId/sources").body()

    /** Marks an assigned (sales) order as delivered with the cash collected from the client. */
    suspend fun deliverSalesOrder(orderId: Int, request: DeliverSalesOrderRequest): ApiResponse<JsonElement> =
        client.postJson("sales-orders/$orderId/deliver", request).body()

    /** Asks for the order to be made editable, with a JPEG (e.g. the receipt) attached. */
    suspend fun requestOrderEdit(orderId: Int, jpeg: ByteArray): ApiResponse<JsonElement> =
        client.postJpeg("sales-orders/$orderId/allow-edits", "image", "image.jpg", jpeg).body()

    /** Changes one product's quantity / unit price on an editable order. */
    suspend fun updateSalesOrderProduct(
        orderId: Int,
        request: UpdateSalesOrderProductRequest
    ): ApiResponse<JsonElement> =
        client.postJson("sales-orders/$orderId/update", request).body()

    /** Removes one product from an editable order. */
    suspend fun deleteSalesOrderProduct(
        orderId: Int,
        request: DeleteSalesOrderProductRequest
    ): ApiResponse<JsonElement> =
        client.postJson("sales-orders/$orderId/delete-product", request).body()

    /** Attaches a JPEG (e.g. the receipt) to the order. */
    suspend fun uploadOrderImage(orderId: Int, jpeg: ByteArray): ApiResponse<JsonElement> =
        client.postJpeg("sales-orders/$orderId/images", "image", "image.jpg", jpeg).body()
}
