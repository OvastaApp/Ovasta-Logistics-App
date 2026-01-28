package com.maxab.basemodule.core.setting.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Country(
    @SerializedName("appsMainSystemBaseUrl")
    val appsMainSystemBaseUrl: String = "max-ab.com",
    @SerializedName("backendBaseUrl")
    val backendBaseUrl: String = "api.maxab.info",
    @SerializedName("logisticsBackendBaseUrl")
    val logisticsBackendBaseUrl: String = "api.maxab.info",
    @SerializedName("commerceBaseUrl")
    val commerceBaseUrl: String = "api.maxab.info",
    @SerializedName("countryCode")
    val countryCode: String = "002",
    @SerializedName("currencyAr")
    val currencyAr: String = "ج.م",
    @SerializedName("currencyEn")
    val currencyEn: String = "EGP",
    @SerializedName("identifier")
    val identifier: String = "EG",
    @SerializedName("imageUrl")
    val imageUrl: String = "https://cdn.maxab.info/eyJidWNrZXQiOiJtYXhhYiIsImtleSI6Imljb25zL2dlbmVyYWwvZWd5cHQucG5nIiwiZWRpdHMiOnsicmVzaXplIjp7IndpZHRoIjoxNTAsImZpdCI6ImNvbnRhaW4ifX19",
    @SerializedName("maxPhoneNumberDigits")
    val maxPhoneNumberDigits: Int = 11,
    @SerializedName("nameAr")
    val nameAr: String = "مصر",
    @SerializedName("nameEn")
    val nameEn: String = "Egypt",
    @SerializedName("name")
    val name: String = "Egypt"
) : Parcelable
