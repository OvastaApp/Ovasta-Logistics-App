package com.ovasta.logisticsapp.base.ext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DateExtTest {

    // 2026-10-18 08:18:22 UTC
    private val expectedMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(2026, Calendar.OCTOBER, 18, 8, 18, 22)
    }.timeInMillis

    @Test
    fun `parses ISO 8601 with Z`() {
        assertEquals(expectedMillis, "2026-10-18T08:18:22Z".toEpochMillisOrNull())
    }

    @Test
    fun `parses ISO 8601 with microseconds`() {
        assertEquals(expectedMillis, "2026-10-18T08:18:22.000000Z".toEpochMillisOrNull())
    }

    @Test
    fun `parses ISO 8601 with offsets`() {
        assertEquals(expectedMillis, "2026-10-18T11:18:22+03:00".toEpochMillisOrNull())
        assertEquals(expectedMillis, "2026-10-18T11:18:22+0300".toEpochMillisOrNull())
    }

    @Test
    fun `parses date without offset in Cairo time whatever the device time zone`() {
        val default = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
            assertEquals(expectedMillis, "2026-10-18 11:18:22".toEpochMillisOrNull())
        } finally {
            TimeZone.setDefault(default)
        }
    }

    @Test
    fun `delivery-orders assigned_at matches created_at from the real response`() {
        // Same order: assigned_at is Cairo time, created_at is UTC
        assertEquals(
            "2026-10-18T08:55:37.000000Z".toEpochMillisOrNull(),
            "2026-10-18 11:55:37".toEpochMillisOrNull()
        )
    }

    @Test
    fun `returns null for unreadable dates`() {
        assertNull("not a date".toEpochMillisOrNull())
        assertNull("".toEpochMillisOrNull())
    }
}
