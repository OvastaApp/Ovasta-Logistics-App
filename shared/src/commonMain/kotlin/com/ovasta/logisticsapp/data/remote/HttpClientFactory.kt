package com.ovasta.logisticsapp.data.remote

import com.ovasta.logisticsapp.base.exception.APIException
import com.ovasta.logisticsapp.base.exception.NetworkException
import com.ovasta.logisticsapp.data.RemoteConstants
import com.ovasta.logisticsapp.data.RemoteConstants.HeadersConst.ACCEPT
import com.ovasta.logisticsapp.data.RemoteConstants.HeadersConst.AUTHORIZATION
import com.ovasta.logisticsapp.data.RemoteConstants.HeadersConst.IDENTIFIER
import com.ovasta.logisticsapp.data.RemoteConstants.HeadersConst.LANG
import com.ovasta.logisticsapp.data.apiJson
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

const val BASE_URL = "http://167.172.209.252/api/logistics/"

private const val IDENTIFIER_HASH = "\$2a\$12\$BeuZVyrk1vlnlws5ljkRnuHA5UypUwVW3gyGoFaGvpdF5sgeSzXr2"

/** Localized texts for network errors; each app supplies them from its own strings. */
interface NetworkErrorMessages {
    fun timeout(): String
    fun genericUnknown(): String
}

/** The logged-in user's access token, read on every request; empty when logged out. */
fun interface AccessTokenProvider {
    fun accessToken(): String
}

/**
 * The client for the logistics backend. It behaves like the OkHttp interceptors it replaced:
 * - every request sends Accept, identifier, lang and the Bearer token
 * - a response without a JSON body is an [APIException], even with a 2xx status
 * - 500 / 503 become an [APIException] with the timeout text; other error statuses use the
 *   body's "msg" / "message"
 * - failures before a response become a [NetworkException]: the timeout text for timeouts,
 *   the generic text otherwise
 *
 * @param logger logs requests and responses with their bodies; null in release builds
 */
fun createHttpClient(
    engine: HttpClientEngine,
    errorMessages: NetworkErrorMessages,
    tokenProvider: AccessTokenProvider,
    logger: Logger? = null,
    json: Json = apiJson,
): HttpClient = HttpClient(engine) {
    expectSuccess = false

    install(ContentNegotiation) { json(json) }

    install(HttpTimeout) {
        connectTimeoutMillis = RemoteConstants.CONNECT_TIMEOUT * 1000
        socketTimeoutMillis = RemoteConstants.READ_TIMEOUT * 1000
    }

    defaultRequest {
        url(BASE_URL)
        header(ACCEPT, "application/json")
        header(IDENTIFIER, IDENTIFIER_HASH)
        header(LANG, "ar")
    }

    install(createClientPlugin("AccessToken") {
        onRequest { request, _ ->
            request.headers[AUTHORIZATION] = "Bearer ${tokenProvider.accessToken()}"
        }
    })

    HttpResponseValidator {
        validateResponse { response -> response.throwIfError(json, errorMessages) }
        handleResponseExceptionWithRequest { cause, _ -> throw cause.toNetworkException(errorMessages) }
    }

    if (logger != null) {
        install(Logging) {
            this.logger = logger
            level = LogLevel.BODY
        }
    }
}

private suspend fun HttpResponse.throwIfError(json: Json, errorMessages: NetworkErrorMessages) {
    if (contentType()?.contentSubtype?.lowercase() != "json") {
        throw APIException(status.value, status.description)
    }
    if (status.isSuccess()) return
    if (status.value == 500 || status.value == 503) {
        throw APIException(RemoteConstants.SERVER_ERROR, errorMessages.timeout())
    }
    val error = runCatching { json.decodeFromString<ApiErrorBody>(bodyAsText()) }.getOrNull()
    throw APIException(
        code = status.value,
        errorMessage = error?.message ?: errorMessages.genericUnknown(),
        data = error?.data
    )
}

private fun Throwable.toNetworkException(errorMessages: NetworkErrorMessages): Throwable = when (this) {
    is APIException, is NetworkException, is CancellationException -> this
    is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException ->
        NetworkException(errorMessages.timeout())
    is IOException -> NetworkException(errorMessages.genericUnknown())
    else -> this
}

/** Error body of a failed request; the backend sends the text as "msg" or "message". */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
private class ApiErrorBody(
    @SerialName("msg") @JsonNames("message") val message: String? = null,
    @SerialName("data") val data: JsonElement? = null
)
