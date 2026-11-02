package com.maxab.basemodule.core.setting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Country(
    val appsMainSystemBaseUrl: String = "max-ab.com",
    val backendBaseUrl: String = "api.maxab.info",
    val logisticsBackendBaseUrl: String = "api.maxab.info",
    val commerceBaseUrl: String = "api.maxab.info",
    val countryCode: String = "002",
    val currencyAr: String = "ج.م",
    val currencyEn: String = "EGP",
    val identifier: String = "EG",
    val imageUrl: String = "https://cdn.maxab.info/eyJidWNrZXQiOiJtYXhhYiIsImtleSI6Imljb25zL2dlbmVyYWwvZWd5cHQucG5nIiwiZWRpdHMiOnsicmVzaXplIjp7IndpZHRoIjoxNTAsImZpdCI6ImNvbnRhaW4ifX19",
    val maxPhoneNumberDigits: Int = 11,
    val nameAr: String = "مصر",
    val nameEn: String = "Egypt",
    val name: String = "Egypt"
) : Parcelable
