package com.ovasta.logisticsapp.base.ext

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class NumberFormatExtTest {

    @Test
    fun `formatGrouped groups thousands with Western digits`() {
        val default = Locale.getDefault()
        try {
            // The partner statistics must not switch to Arabic-Indic digits in Arabic
            Locale.setDefault(Locale.forLanguageTag("ar"))
            assertEquals("1,234,567", 1_234_567.0.formatGrouped(0))
            assertEquals("1,234.50", 1234.5.formatGrouped(2))
            assertEquals("-20.25", (-20.25).formatGrouped(2))
        } finally {
            Locale.setDefault(default)
        }
    }

    @Test
    fun `formatMonthYear names the month in the device language`() {
        val default = Locale.getDefault()
        try {
            Locale.setDefault(Locale.ENGLISH)
            assertEquals("April 2026", formatMonthYear(2026, 4))
            assertEquals("December 2025", formatMonthYear(2025, 12))
        } finally {
            Locale.setDefault(default)
        }
    }
}
