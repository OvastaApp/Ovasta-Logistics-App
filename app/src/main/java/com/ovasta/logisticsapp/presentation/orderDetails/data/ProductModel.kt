package com.ovasta.logisticsapp.presentation.orderDetails.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class ProductModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("product_id")
    val productId: Int? = null,

    @SerializedName("type")
    val type: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("category_id")
    val categoryId: Int? = null,

    @SerializedName("price")
    val price: Double,

    @SerializedName("unit")
    val unit: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("packing_unit_id")
    val packingUnitId: Int? = null,

    @SerializedName("quantity")
    val quantity: Int? = null,


    @SerializedName("expiry_date")
    val expiryDate: String? = null,

    @SerializedName("reason")
    val reason: String? = null,

    @SerializedName("available_basic_unit_count")
    val availableBasicUnitCount: Int? = null,

    @SerializedName("group_available_count")
    val availableGroupQuantity: Int? = null,

    @SerializedName("units")
    val units: List<ProductUnitModel>? = null,

    @SerializedName("group_quantity")
    val groupQuantity: Int? = null,

    @SerializedName("group_components")
    val bundleComponents: List<BundleComponentModel>? = null,

    var rejectedReasonId: String? = null,

    var allQuantityInWarehouse: Int? = null,

    val oldQuantity: Int? = null
) : Parcelable {

    enum class Type(val type: String) {
        PRODUCT("SINGLE"),
        BUNDLE("GROUP")
    }

    fun isBundle(): Boolean {
        return type == Type.BUNDLE.type
    }

    fun isBundleQuantityEdited(): Boolean {
        return isBundle() && groupQuantity != oldQuantity
    }

}


@Parcelize
@Keep
data class ProductUnitModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("packing_unit_id")
    val packingUnitId: Int,

    @SerializedName("price")
    val price: Double,

    @SerializedName("name")
    val name: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("basic_unit_count")
    val basicUnitCount: Int,

    val oldQuantity: Int? = null
) : Parcelable {

    fun isQuantityEdited(): Boolean {
        return oldQuantity != null && oldQuantity != quantity
    }

}



@Parcelize
@Keep
data class BundleComponentModel(
    @SerializedName("product_name")
    val productName: String,

    @SerializedName("unit_name")
    val unitName: String,

    @SerializedName("quantity")
    val quantity: Int
) : Parcelable