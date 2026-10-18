package com.ovasta.logisticsapp.presentation.orderDetails.data

import com.ovasta.logisticsapp.data.ApiResponse
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DeleteSalesOrderProductRequest
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.DeliverSalesOrderRequest
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.UpdateSalesOrderProductRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface OrderDetailsApi {
    /** Returns the sources the given product can be assigned to. */
    @GET("products/{id}/sources")
    suspend fun getProductSources(
        @Path("id") productId: Int
    ): ApiResponse<List<ProductSource>>

    /** Marks an assigned (sales) order as delivered with the cash collected from the client. */
    @POST("sales-orders/{id}/deliver")
    suspend fun deliverSalesOrder(
        @Path("id") orderId: Int,
        @Body request: DeliverSalesOrderRequest
    ): ApiResponse<Any>

    /** Asks for the order to be made editable, with an image (e.g. the receipt) attached. */
    @Multipart
    @POST("sales-orders/{id}/allow-edits")
    suspend fun requestOrderEdit(
        @Path("id") orderId: Int,
        @Part image: MultipartBody.Part
    ): ApiResponse<Any>

    /** Changes one product's quantity / unit price on an editable order. */
    @POST("sales-orders/{id}/update")
    suspend fun updateSalesOrderProduct(
        @Path("id") orderId: Int,
        @Body request: UpdateSalesOrderProductRequest
    ): ApiResponse<Any>

    /** Removes one product from an editable order. */
    @POST("sales-orders/{id}/delete-product")
    suspend fun deleteSalesOrderProduct(
        @Path("id") orderId: Int,
        @Body request: DeleteSalesOrderProductRequest
    ): ApiResponse<Any>

    /** Attaches an image (e.g. the receipt) to the order. */
    @Multipart
    @POST("sales-orders/{id}/images")
    suspend fun uploadOrderImage(
        @Path("id") orderId: Int,
        @Part image: MultipartBody.Part
    ): ApiResponse<Any>
}
