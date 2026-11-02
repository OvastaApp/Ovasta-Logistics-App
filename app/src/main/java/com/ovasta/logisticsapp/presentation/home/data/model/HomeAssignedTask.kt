package com.ovasta.logisticsapp.presentation.home.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
@Keep
data class HomeAssignedTask(
    @get:PropertyName("id")
    @PropertyName("id")
    val id: Int = 1,
)