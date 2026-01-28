package com.ovasta.logisticsapp.base.exception

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.IOException

class APIException(
    @Expose var code: Int?,
    @SerializedName("msg", alternate = ["message"]) val errorMessage: String,
    @SerializedName("data") val data: Any? = null
) : IOException()
