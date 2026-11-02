package com.maxab.basemodule.core.setting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class RemoteConfigModel(
    val forceUpdateMessage: String = "",
    val forceUpdateTitle: String = "",
    val forceVersion: String = "",
    val forceVersionSandbox: String = "",
    val update: String = "",
    val updateSandBox: String = "",
    val collectionAmountUpTo: String = "1"
) : Parcelable