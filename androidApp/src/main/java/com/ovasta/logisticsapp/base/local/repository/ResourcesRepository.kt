package com.ovasta.logisticsapp.base.local.repository

import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.local.AppResources
import com.ovasta.logisticsapp.data.remote.NetworkErrorMessages

class ResourcesRepository(private val appResources: AppResources) : NetworkErrorMessages {

    override fun timeout(): String = getSocketTimeoutExceptionMessage()
    override fun genericUnknown(): String = getGenericUnknownMessage()

    fun getNetworkExceptionMessage(): String = appResources.getString(R.string.no_internet_connection)
    fun getSocketTimeoutExceptionMessage(): String = appResources.getString(R.string.timeout_error_message)
    fun getGenericUnknownMessage(): String = appResources.getString(R.string.generic_unknown_error)
}