package com.maxab.basemodule.core.setting.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class RemoteConfigModel(
    @SerializedName("force_update_message")
    val forceUpdateMessage: String = "",
    @SerializedName("force_update_title")
    val forceUpdateTitle: String = "",
    @SerializedName("force_version")
    val forceVersion: String = "",
    @SerializedName("force_version_sandbox")
    val forceVersionSandbox: String = "",
    @SerializedName("update")
    val update: String = "",
    @SerializedName("update_sandBox")
    val updateSandBox: String = "",
    @SerializedName("COLLECTION_AMOUNT_UP_TO")
    val collectionAmountUpTo: String = "1"
) : Parcelable