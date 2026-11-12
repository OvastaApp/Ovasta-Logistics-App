package com.ovasta.logisticsapp.base.ext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

actual fun Double.formatGrouped(decimals: Int): String =
    String.format(Locale.ENGLISH, "%,.${decimals}f", this)

actual fun formatMonthYear(year: Int, month: Int): String {
    val date = Calendar.getInstance().apply {
        clear()
        set(year, month - 1, 1)
    }.time
    return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
}
