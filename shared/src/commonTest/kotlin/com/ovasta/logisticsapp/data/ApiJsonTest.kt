package com.ovasta.logisticsapp.data

import com.ovasta.logisticsapp.presentation.home.data.model.ChangeStatusRequest
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatistics
import com.ovasta.logisticsapp.presentation.home.data.model.PartnerStatus
import com.ovasta.logisticsapp.presentation.profile.data.model.WalletTransaction
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.Test

class ApiJsonTest {

    @Test
    fun decodesLoginResponseWithSnakeCaseUserAndTopLevelToken() {
        val response = apiJson.decodeFromString<ApiResponse<User>>(
            """
            {"status":200,"message":"ok","token":"abc","extra":1,
             "data":{"id":5,"delivery_id":7,"district_id":3,"name":"Ali","mobile":"0100",
                     "email":null,"type_id":1,"available":true,"is_outsource":true}}
            """
        )
        val user = response.data!!
        assertEquals("abc", response.token)
        assertEquals(7, user.deliveryId)
        assertEquals(3, user.districtId)
        assertEquals(1, user.userTypeId)
        assertTrue(user.isOutsource)
        assertNull(user.email)
    }

    @Test
    fun missingDataAndWrapperFieldsFallBackToDefaults() {
        val response = apiJson.decodeFromString<ApiResponse<JsonElement>>("""{"status":200}""")
        assertNull(response.data)
        assertEquals("", response.message)
        assertEquals("", response.token)
    }

    @Test
    fun nullForANonNullWrapperFieldUsesItsDefault() {
        val response = apiJson.decodeFromString<ApiResponse<PartnerStatus>>(
            """{"status":200,"message":null,"data":{"status":true}}"""
        )
        assertEquals("", response.message)
        assertTrue(response.data!!.isOnline)
    }

    @Test
    fun anyDataShapeDecodesForEndpointsWhoseDataIsIgnored() {
        listOf("""[]""", """{}""", """"done"""", """null""").forEach { data ->
            apiJson.decodeFromString<ApiResponse<JsonElement>>("""{"status":200,"data":$data}""")
        }
    }

    @Test
    fun decodesPaginatedAndNestedLists() {
        val page = apiJson.decodeFromString<ApiResponse<PaginatedData<WalletTransaction>>>(
            """{"data":{"current_page":1,"last_page":2,"total":3,
                "data":[{"id":1,"amount":-20.5,"type":"delivery_debit"}]}}"""
        ).data!!
        assertTrue(page.hasNextPage)
        assertFalse(page.data!!.single().isCredit)

        val stats = apiJson.decodeFromString<PartnerStatistics>(
            """{"orders_count":"4","incentives":{"milestones":null}}"""
        )
        assertEquals(4, stats.ordersCount)
        assertTrue(stats.incentives!!.milestones.isEmpty())
    }

    @Test
    fun requestBodiesLeaveOutNullFields() {
        assertEquals(
            """{"online":false}""",
            apiJson.encodeToString(ChangeStatusRequest(isOnline = false))
        )
        assertEquals(
            """{"online":true,"lat":30.1,"long":31.2}""",
            apiJson.encodeToString(ChangeStatusRequest(isOnline = true, lat = 30.1, long = 31.2))
        )
    }

    @Test
    fun readsAUserSavedInTheSessionBeforeTheSnakeCaseNames() {
        // SessionPreferences decodes with its own Json, which only ignores unknown keys
        val sessionJson = Json { ignoreUnknownKeys = true }
        val user = sessionJson.decodeFromString<User>(
            """{"id":5,"deliveryId":7,"districtId":3,"name":"Ali","mobile":"0100","email":null,
                "userTypeId":1,"available":true,"isOutsource":true,"token":"abc"}"""
        )
        assertEquals(7, user.deliveryId)
        assertEquals(3, user.districtId)
        assertEquals(1, user.userTypeId)
        assertTrue(user.isOutsource)
        assertEquals("abc", user.token)

        val roundTrip = sessionJson.decodeFromString<User>(Json.encodeToString(user))
        assertEquals(7, roundTrip.deliveryId)
    }
}
