package com.ovasta.logisticsapp.base.ext

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.text.matches

/**
 * Formats a number with at most [maxDecimals] decimals and no trailing zeros, using Western
 * digits: 2.0 -> "2", 0.250 -> "0.25", 20.25 -> "20.25". Also hides floating-point noise
 * such as 0.30000000000000004.
 */
fun Double.formatDecimal(maxDecimals: Int = 3): String =
    BigDecimal.valueOf(this).setScale(maxDecimals, RoundingMode.HALF_UP)
        .stripTrailingZeros().toPlainString()

/**
 * Whether [input] is an empty string or a number being typed with up to [maxIntegerDigits]
 * digits and up to [maxDecimals] decimals (e.g. "0.", ".5", "20.25"). For decimal text fields.
 */
fun isValidDecimalInput(input: String, maxIntegerDigits: Int, maxDecimals: Int): Boolean =
    input.isEmpty() ||
        input.matches(Regex("^\\d{0,$maxIntegerDigits}(\\.\\d{0,$maxDecimals})?$"))


fun String?.orDefault(default: String = ""): String = this ?: default

fun Int?.orDefault(default: Int = 0): Int = this ?: default

fun Long?.orDefault(default: Long = 0L): Long = this ?: default

fun Float?.orDefault(default: Float = 0f): Float = this ?: default

fun Double?.orDefault(default: Double = 0.0): Double = this ?: default

fun Boolean?.orDefault(default: Boolean = false): Boolean = this ?: default

fun <T> T?.orDefault(default: T): T = this ?: default

fun String.isDecimalNumber(): Boolean {
    return this.matches(Regex("^\\d+(\\.\\d*)?$"))
}