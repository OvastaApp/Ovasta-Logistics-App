package com.ovasta.logisticsapp.data.remote

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.ios
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNull

// The iOS SDK returns NSNull for a field stored as null
@OptIn(ExperimentalForeignApi::class)
internal actual fun DocumentSnapshot.hasNonNull(field: String): Boolean =
    ios.valueForField(field).let { it != null && it !is NSNull }
