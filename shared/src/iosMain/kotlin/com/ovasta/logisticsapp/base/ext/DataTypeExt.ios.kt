package com.ovasta.logisticsapp.base.ext

import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSDecimalNumberHandler
import platform.Foundation.NSRoundingMode

// Like Android's BigDecimal.valueOf(this) (the shortest decimal text of the double), rounded
// half-up; stringValue prints no trailing zeros and no exponent, with Western digits
actual fun Double.formatDecimal(maxDecimals: Int): String {
    val rounding = NSDecimalNumberHandler(
        roundingMode = NSRoundingMode.NSRoundPlain,
        scale = maxDecimals.toShort(),
        raiseOnExactness = false,
        raiseOnOverflow = false,
        raiseOnUnderflow = false,
        raiseOnDivideByZero = false
    )
    return NSDecimalNumber(string = this.toString())
        .decimalNumberByRoundingAccordingToBehavior(rounding)
        .stringValue
}
