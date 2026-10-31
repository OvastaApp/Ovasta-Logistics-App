package com.ovasta.logisticsapp.base.ext

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// The backend sends dates without an offset (e.g. delivery-orders "assigned_at") in Cairo time
private val SERVER_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Africa/Cairo")

private val SERVER_DATE_PATTERNS = listOf(
    "yyyy-MM-dd'T'HH:mm:ssXXX", // 2026-10-18T11:18:22+03:00
    "yyyy-MM-dd'T'HH:mm:ssXX",  // 2026-10-18T11:18:22+0300
    "yyyy-MM-dd'T'HH:mm:ss"     // 2026-10-18T11:18:22 (server time zone)
)

/**
 * Parses a server date string into epoch millis, or null if it can't be read.
 * Accepts ISO 8601 with or without fractional seconds / offset, and "yyyy-MM-dd HH:mm:ss".
 * Dates without an offset are read in the server's time zone, whatever the device's is.
 */
fun String.toEpochMillisOrNull(): Long? {
    // SimpleDateFormat can't read microseconds, so drop the fraction; normalise "Z" and the space separator
    val normalized = trim()
        .replace(' ', 'T')
        .replace(Regex("\\.\\d+"), "")
        .replace(Regex("Z$"), "+00:00")

    // Offset patterns go first: parse() ignores trailing text, so the plain pattern would drop the offset
    return SERVER_DATE_PATTERNS.firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply { timeZone = SERVER_TIME_ZONE }
                .parse(normalized)?.time
        }.getOrNull()
    }
}

/**
 * Formats a server date's day as "31/10/2026" in the device's time zone, or returns it unchanged
 * if it can't be read. Uses Western digits, like the rest of the app's numbers.
 */
fun String.toDisplayDate(): String = formatServerDate("dd/MM/yyyy") ?: this

/**
 * Formats a server date's time as "02:05 PM" in the device's time zone, or null if it can't be
 * read. Kept left-to-right so "PM" stays after the time inside Arabic text.
 */
fun String.toDisplayTime(): String? = formatServerDate("hh:mm a")?.let { "⁦$it⁩" }

private fun String.formatServerDate(pattern: String): String? =
    toEpochMillisOrNull()?.let { SimpleDateFormat(pattern, Locale.ENGLISH).format(Date(it)) }
