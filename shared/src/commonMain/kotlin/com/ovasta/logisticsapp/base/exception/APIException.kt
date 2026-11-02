package com.ovasta.logisticsapp.base.exception

import kotlinx.io.IOException

class APIException(
    var code: Int?,
    val errorMessage: String,
    val data: Any? = null
) : IOException()
