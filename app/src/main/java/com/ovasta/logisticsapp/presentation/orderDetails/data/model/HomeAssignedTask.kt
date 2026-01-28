package com.ovasta.logisticsapp.presentation.orderDetails.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
@Keep
data class HomeAssignedTask(
    @get:PropertyName("id")
    @PropertyName("id")
    val id: Int = 1,
) : Parcelable