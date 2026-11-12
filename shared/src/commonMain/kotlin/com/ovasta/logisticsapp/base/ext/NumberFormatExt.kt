package com.ovasta.logisticsapp.base.ext

/** [this] with comma grouping and exactly [decimals] decimals, in Western digits: 1,234.50. */
expect fun Double.formatGrouped(decimals: Int): String

/** "April 2026" for [year] and [month] (1-12), in the device's language. */
expect fun formatMonthYear(year: Int, month: Int): String
