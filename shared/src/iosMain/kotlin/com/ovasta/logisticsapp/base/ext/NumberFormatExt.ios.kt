package com.ovasta.logisticsapp.base.ext

import platform.Foundation.NSCalendar
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale

actual fun Double.formatGrouped(decimals: Int): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
        locale = NSLocale(localeIdentifier = "en_US_POSIX")
        usesGroupingSeparator = true
        groupingSeparator = ","
        decimalSeparator = "."
        minimumFractionDigits = decimals.toULong()
        maximumFractionDigits = decimals.toULong()
    }
    return formatter.stringFromNumber(NSNumber(double = this)) ?: toString()
}

actual fun formatMonthYear(year: Int, month: Int): String {
    val components = NSDateComponents().apply {
        this.year = year.toLong()
        this.month = month.toLong()
        this.day = 1
    }
    val date = NSCalendar.currentCalendar.dateFromComponents(components) ?: return "$month/$year"
    val formatter = NSDateFormatter().apply {
        dateFormat = "MMMM yyyy"
        locale = NSLocale.currentLocale
    }
    return formatter.stringFromDate(date)
}
