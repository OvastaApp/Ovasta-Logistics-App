package com.ovasta.logisticsapp.data

import kotlinx.serialization.json.Json

// Lenient like Gson was: unknown keys, nulls and missing fields fall back to defaults,
// null fields are left out of request bodies
val apiJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
    encodeDefaults = true
    isLenient = true
}
