package com.ovasta.logisticsapp.base.interceptor

import com.ovasta.logisticsapp.base.exception.NetworkException
import com.ovasta.logisticsapp.data.RemoteConstants
import com.ovasta.logisticsapp.base.exception.APIException
import com.ovasta.logisticsapp.base.local.repository.ResourcesRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.lang.Long
import java.net.SocketTimeoutException
import java.nio.charset.Charset


class ErrorMappingInterceptor(
    private val resourcesRepository: ResourcesRepository,
    private val json: Json
) : Interceptor {

    private val keyJson = "json"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response

        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            if (e is SocketTimeoutException) {
                throw NetworkException(resourcesRepository.getSocketTimeoutExceptionMessage())
            } else {
                throw NetworkException(resourcesRepository.getGenericUnknownMessage())
            }
        }
        val body = response.body!!
        // Only intercept JSON type responses and ignore the rest.
        if (isJsonTypeResponse(body)) {
            val source = body.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer
            val charset = body.contentType()!!.charset(Charset.forName("UTF-8"))!!
            // Clone the existing buffer is they can only read once so we still want to pass the original one to the chain.
            val responseBody = buffer.clone().readString(charset)
            if (response.isSuccessful.not()) {
                if (response.code == 500 || response.code == 503) {
                    throw APIException(
                        RemoteConstants.SERVER_ERROR,
                        resourcesRepository.getSocketTimeoutExceptionMessage()
                    )
                } else {
                    val error = runCatching {
                        json.decodeFromString<ApiErrorBody>(responseBody)
                    }.getOrNull()
                    throw APIException(
                        code = response.code,
                        errorMessage = error?.message ?: resourcesRepository.getGenericUnknownMessage(),
                        data = error?.data
                    )
                }
            }
        } else {
            throw APIException(
                response.code,
                response.message
            )
        }

        return response
    }

    private fun isJsonTypeResponse(body: ResponseBody?): Boolean {
        return body?.contentType() != null && body.contentType()!!.subtype.lowercase() == keyJson
    }

    /** Error body of a failed request; the backend sends the text as "msg" or "message". */
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    private class ApiErrorBody(
        @SerialName("msg") @JsonNames("message") val message: String? = null,
        @SerialName("data") val data: JsonElement? = null
    )
}