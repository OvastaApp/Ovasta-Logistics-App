package com.ovasta.logisticsapp.data.remote

import com.ovasta.logisticsapp.base.exception.APIException
import com.ovasta.logisticsapp.base.exception.NetworkException
import com.ovasta.logisticsapp.presentation.home.data.HomeApi
import com.ovasta.logisticsapp.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.logisticsapp.presentation.orderDetails.data.OrderDetailsApi
import com.ovasta.logisticsapp.presentation.profile.data.WalletApi
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HttpClientFactoryTest {

    private val errorMessages = object : NetworkErrorMessages {
        override fun timeout() = "timeout text"
        override fun genericUnknown() = "unknown text"
    }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    private val requests = mutableListOf<HttpRequestData>()

    private fun client(token: () -> String = { "token-1" }, handler: MockRequestHandler) =
        createHttpClient(
            engine = MockEngine { request ->
                requests += request
                handler(request)
            },
            errorMessages = errorMessages,
            tokenProvider = { token() },
        )

    private fun okJson(body: String = """{"status":200}"""): MockRequestHandler =
        { respond(body, HttpStatusCode.OK, jsonHeaders) }

    @Test
    fun sendsTheBackendHeadersAndResolvesPathsAgainstTheBaseUrl() = runTest {
        val api = HomeApi(client(handler = okJson("""{"status":200,"data":{"status":true}}""")))

        val response = api.getPartnerStatus()

        assertTrue(response.data!!.isOnline)
        val request = requests.single()
        assertEquals("http://167.172.209.252/api/logistics/get-partner-status", request.url.toString())
        assertEquals(HttpMethod.Get, request.method)
        assertEquals("Bearer token-1", request.headers[HttpHeaders.Authorization])
        assertEquals("application/json", request.headers.getAll(HttpHeaders.Accept)?.joinToString())
        assertEquals("ar", request.headers["lang"])
        assertNotNull(request.headers["identifier"])
    }

    @Test
    fun readsTheTokenOnEveryRequest() = runTest {
        var token = "before-login"
        val api = HomeApi(client(token = { token }, handler = okJson()))

        api.getPartnerStatus()
        token = "after-login"
        api.getPartnerStatus()

        assertEquals(
            listOf("Bearer before-login", "Bearer after-login"),
            requests.map { it.headers[HttpHeaders.Authorization] }
        )
    }

    @Test
    fun sendsQueryParametersAndJsonBodies() = runTest {
        val api = HomeApi(client(handler = okJson()))

        api.getPartnerStatistics(month = 5, year = 2026)
        api.changePartnerStatus(ChangeStatusRequest(isOnline = true, lat = 30.1))

        assertEquals(
            "http://167.172.209.252/api/logistics/partner-statistics?month=5&year=2026",
            requests[0].url.toString()
        )
        val body = requests[1].body as TextContent
        assertEquals(HttpMethod.Post, requests[1].method)
        assertEquals("""{"online":true,"lat":30.1}""", body.text)
        assertEquals("application/json", body.contentType.withoutParameters().toString())
    }

    @Test
    fun uploadsImagesAsMultipartJpegParts() = runTest {
        val orderApi = OrderDetailsApi(client(handler = okJson()))
        val walletApi = WalletApi(client(handler = okJson()))

        orderApi.uploadOrderImage(orderId = 7, jpeg = byteArrayOf(1, 2, 3))
        walletApi.createTopUpRequest(amount = "150.5", proof = byteArrayOf(4, 5))

        assertEquals("http://167.172.209.252/api/logistics/sales-orders/7/images", requests[0].url.toString())
        val imageForm = requests[0].body.toByteArray().decodeToString()
        assertTrue("name=image" in imageForm || "name=\"image\"" in imageForm, imageForm)
        assertTrue("filename=\"image.jpg\"" in imageForm, imageForm)
        assertTrue("Content-Type: image/jpeg" in imageForm, imageForm)

        val topUpForm = requests[1].body.toByteArray().decodeToString()
        assertTrue("filename=\"proof.jpg\"" in topUpForm, topUpForm)
        assertTrue("150.5" in topUpForm, topUpForm)
    }

    @Test
    fun errorStatusUsesTheBodyMessage() = runTest {
        val api = HomeApi(client {
            respond("""{"msg":"Order already taken"}""", HttpStatusCode.UnprocessableEntity, jsonHeaders)
        })

        val error = assertFailsWith<APIException> { api.acceptDeliveryOrder(1) }

        assertEquals(422, error.code)
        assertEquals("Order already taken", error.errorMessage)
    }

    @Test
    fun errorStatusAlsoReadsMessageKey() = runTest {
        val api = HomeApi(client {
            respond("""{"message":"Unauthenticated."}""", HttpStatusCode.Unauthorized, jsonHeaders)
        })

        val error = assertFailsWith<APIException> { api.getHome() }

        assertEquals(401, error.code)
        assertEquals("Unauthenticated.", error.errorMessage)
    }

    @Test
    fun errorStatusWithoutMessageUsesTheGenericText() = runTest {
        val api = HomeApi(client { respond("""{}""", HttpStatusCode.BadRequest, jsonHeaders) })

        val error = assertFailsWith<APIException> { api.getHome() }

        assertEquals(400, error.code)
        assertEquals("unknown text", error.errorMessage)
    }

    @Test
    fun serverErrorsBecome500WithTheTimeoutText() = runTest {
        listOf(HttpStatusCode.InternalServerError, HttpStatusCode.ServiceUnavailable).forEach { status ->
            val api = HomeApi(client { respond("""{"msg":"boom"}""", status, jsonHeaders) })

            val error = assertFailsWith<APIException> { api.getHome() }

            assertEquals(500, error.code)
            assertEquals("timeout text", error.errorMessage)
        }
    }

    @Test
    fun responseWithoutJsonIsAnErrorEvenWhenSuccessful() = runTest {
        val api = HomeApi(client {
            respond("<html/>", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "text/html"))
        })

        val error = assertFailsWith<APIException> { api.getHome() }

        assertEquals(200, error.code)
    }

    @Test
    fun timeoutsBecomeNetworkExceptionWithTheTimeoutText() = runTest {
        val api = HomeApi(client { throw HttpRequestTimeoutException("url", 30_000) })

        val error = assertFailsWith<NetworkException> { api.getHome() }

        assertEquals("timeout text", error.message)
    }

    @Test
    fun otherNetworkFailuresBecomeNetworkExceptionWithTheGenericText() = runTest {
        val api = HomeApi(client { throw IOException("no route to host") })

        val error = assertFailsWith<NetworkException> { api.getHome() }

        assertEquals("unknown text", error.message)
    }
}
