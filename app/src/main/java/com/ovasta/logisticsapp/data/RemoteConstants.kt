package com.ovasta.logisticsapp.data

import org.koin.core.component.KoinComponent

object RemoteConstants : KoinComponent {
    const val CONNECT_TIMEOUT: Long = 30
    const val READ_TIMEOUT: Long = 30
    const val WRITE_TIMEOUT: Long = 30
    const val TIME_OUT_STATUS_CODE = -1
    const val LOST_CONNECTION_STATUS_CODE = -2
    const val SERVER_ERROR = 500
    const val UNAUTHORIZED_CODE = 401
    const val ECOMMERCE_END_POINT = "commerce/api/open/v1"
    const val CACHE_NAME = "maxman_http_cache"
    const val CACHE_SIZE = 50 * 1024 * 1024L

    //header constant
    object HeadersConst{
        const val VERSION = "Version"
        const val DEVICE_MODEL = "device_model"
        const val ANDROID_VERSION = "android_version"
        const val ACCEPT = "Accept"
        const val IDENTIFIER = "identifier"
        const val DEVICE_ID = "device-id"
        const val AUTHORIZATION = "Authorization"
        const val LANG = "lang"
    }
}