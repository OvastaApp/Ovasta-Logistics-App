package com.ovasta.logisticsapp.base.ext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DataTypeExtTest {

    @Test
    fun `formatDecimal drops trailing zeros`() {
        assertEquals("2", 2.0.formatDecimal())
        assertEquals("0.5", 0.5.formatDecimal())
        assertEquals("0.25", 0.250.formatDecimal())
        assertEquals("20.25", 20.25.formatDecimal(maxDecimals = 2))
    }

    @Test
    fun `formatDecimal hides floating point noise`() {
        assertEquals("0.3", (0.1 * 3).formatDecimal())
        assertEquals("10.13", (20.25 * 0.5).formatDecimal(maxDecimals = 2))
    }

    @Test
    fun `isValidDecimalInput accepts numbers being typed`() {
        listOf("", "0", "0.", ".5", "0.250", "20.25", "1234").forEach {
            assertTrue(it, isValidDecimalInput(it, maxIntegerDigits = 4, maxDecimals = 3))
        }
    }

    @Test
    fun `isValidDecimalInput rejects too many digits and bad characters`() {
        listOf("0.2500", "12345", "1.2.3", "-1", "1,5", "abc").forEach {
            assertFalse(it, isValidDecimalInput(it, maxIntegerDigits = 4, maxDecimals = 3))
        }
    }
}
