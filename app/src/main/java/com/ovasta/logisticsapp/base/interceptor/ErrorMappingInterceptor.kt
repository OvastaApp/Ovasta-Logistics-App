package com.ovasta.logisticsapp.base.interceptor

import com.ovasta.logisticsapp.base.exception.NetworkException
import com.ovasta.logisticsapp.data.RemoteConstants
import com.google.gson.Gson
import com.ovasta.logisticsapp.base.exception.APIException
import com.ovasta.logisticsapp.base.local.repository.ResourcesRepository
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.lang.Long
import java.net.SocketTimeoutException
import java.nio.charset.Charset


class ErrorMappingInterceptor(
    private val resourcesRepository: ResourcesRepository,
    private val gsonPareser: Gson
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
                    val apiException = gsonPareser.fromJson(responseBody, APIException::class.java)
                    apiException.code = response.code
                    throw apiException
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
}