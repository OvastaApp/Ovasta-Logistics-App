package com.ovasta.logisticsapp.presentation.orderDetails.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class ReasonModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("value")
    val value: String,

    val isSelected: Boolean = false
):Parcelable