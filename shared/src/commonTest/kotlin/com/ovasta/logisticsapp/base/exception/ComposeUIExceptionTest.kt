package com.ovasta.logisticsapp.base.exception

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.generic_unknown_error
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ComposeUIExceptionTest {

    @Test
    fun apiErrorsKeepTheirCodeAndMessage() {
        val retry = {}

        val ui = APIException(code = 422, errorMessage = "Order already taken").toComposeUIException(retry)

        assertEquals(422, ui.code)
        assertEquals("Order already taken", ui.errorMessage)
        assertNull(ui.exceptionMessageResource)
        assertEquals(listOf(retry), ui.actions)
    }

    @Test
    fun networkErrorsShowTheirLocalizedText() {
        val ui = NetworkException("timeout text").toComposeUIException()

        assertEquals(0, ui.code)
        assertEquals("timeout text", ui.errorMessage)
    }

    @Test
    fun anythingElseShowsTheGenericError() {
        val ui = IllegalStateException("boom").toComposeUIException()

        assertEquals(0, ui.code)
        assertNull(ui.errorMessage)
        assertEquals(Res.string.generic_unknown_error, ui.exceptionMessageResource)
    }

    @Test
    fun aComposeUIExceptionIsPassedThrough() {
        val original = ComposeUIException(errorMessage = "x", code = 1)

        assertSame(original, original.toComposeUIException())
    }
}
