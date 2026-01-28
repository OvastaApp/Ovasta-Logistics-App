package com.ovasta.logisticsapp.base.local.repository

import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.local.AppResources

class ResourcesRepository(private val appResources: AppResources) {

    fun getNetworkExceptionMessage(): String = appResources.getString(R.string.no_internet_connection)
    fun getSocketTimeoutExceptionMessage(): String = appResources.getString(R.string.timeout_error_message)
    fun getGenericUnknownMessage(): String = appResources.getString(R.string.generic_unknown_error)
}