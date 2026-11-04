package com.ovasta.logisticsapp.data.setting.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigModel(
    val forceUpdateMessage: String = "",
    val forceUpdateTitle: String = "",
    val forceVersion: String = "",
    val forceVersionSandbox: String = "",
    val update: String = "",
    val updateSandBox: String = "",
    val collectionAmountUpTo: String = "1"
)