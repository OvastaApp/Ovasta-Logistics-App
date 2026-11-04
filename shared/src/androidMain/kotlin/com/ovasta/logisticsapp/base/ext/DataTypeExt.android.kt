package com.ovasta.logisticsapp.base.ext

import java.math.BigDecimal
import java.math.RoundingMode

actual fun Double.formatDecimal(maxDecimals: Int): String =
    BigDecimal.valueOf(this).setScale(maxDecimals, RoundingMode.HALF_UP)
        .stripTrailingZeros().toPlainString()
