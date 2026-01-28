package com.maxab.basemodule.core.setting.model

import android.os.Parcelable
import androidx.annotation.DrawableRes

import kotlinx.parcelize.Parcelize

import com.google.gson.annotations.SerializedName


@Parcelize
data class Language(
    @SerializedName("code")
    val code: String,
    @SerializedName("default")
    val default: Boolean,
    @SerializedName("direction")
    val direction: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,

    @DrawableRes var flag: Int = 0
) : Parcelable