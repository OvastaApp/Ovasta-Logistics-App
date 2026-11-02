package com.ovasta.logisticsapp.base.ext

import java.text.NumberFormat
import java.util.Locale

const val FRACTION_DIGITS = 2

fun Float.formatPrice(fractionDigits: Int?): String {
    val formater = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = fractionDigits ?: FRACTION_DIGITS
        isGroupingUsed = true
    }
    val formatedPrice = formater.format(this)
    return formatedPrice
}