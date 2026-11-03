package com.ovasta.logisticsapp.base.ext

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// The backend sends dates without an offset (e.g. delivery-orders "assigned_at") in Cairo time
private val SERVER_TIME_ZONE = TimeZone.of("Africa/Cairo")

// 31/10/2026
private val DISPLAY_DATE_FORMAT = LocalDate.Format {
    day(); char('/'); monthNumber(); char('/'); year()
}

// 02:05 PM
private val DISPLAY_TIME_FORMAT = LocalTime.Format {
    amPmHour(); char(':'); minute(); char(' '); amPmMarker("AM", "PM")
}

/** Today's date in the device's time zone. */
@OptIn(ExperimentalTime::class)
fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

/**
 * Parses a server date string into epoch millis, or null if it can't be read.
 * Accepts ISO 8601 with or without fractional seconds / offset, and "yyyy-MM-dd HH:mm:ss".
 * Dates without an offset are read in the server's time zone, whatever the device's is.
 */
@OptIn(ExperimentalTime::class)
fun String.toEpochMillisOrNull(): Long? {
    // Drop the fraction (millis are enough); normalise "Z", "+0300" and the space separator
    val normalized = trim()
        .replace(' ', 'T')
        .replace(Regex("\\.\\d+"), "")
        .replace(Regex("Z$"), "+00:00")
        .replace(Regex("([+-]\\d{2})(\\d{2})$"), "$1:$2")

    return runCatching { Instant.parse(normalized) }
        .recoverCatching { LocalDateTime.parse(normalized).toInstant(SERVER_TIME_ZONE) }
        .getOrNull()
        ?.toEpochMilliseconds()
}

/**
 * Formats a server date's day as "31/10/2026" in the device's time zone, or returns it unchanged
 * if it can't be read. Uses Western digits, like the rest of the app's numbers.
 */
fun String.toDisplayDate(): String = toDeviceDateTime()?.date?.format(DISPLAY_DATE_FORMAT) ?: this

/**
 * Formats a server date's time as "02:05 PM" in the device's time zone, or null if it can't be
 * read. Kept left-to-right so "PM" stays after the time inside Arabic text.
 */
fun String.toDisplayTime(): String? = toDeviceDateTime()?.time?.format(DISPLAY_TIME_FORMAT)?.let { "⁦$it⁩" }

@OptIn(ExperimentalTime::class)
private fun String.toDeviceDateTime(): LocalDateTime? = toEpochMillisOrNull()?.let {
    Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
}

/** Milliseconds since the epoch, like `System.currentTimeMillis()`. */
@OptIn(ExperimentalTime::class)
fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
