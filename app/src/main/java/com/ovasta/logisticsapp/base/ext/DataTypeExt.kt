package com.ovasta.logisticsapp.base.ext

import kotlin.text.matches


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