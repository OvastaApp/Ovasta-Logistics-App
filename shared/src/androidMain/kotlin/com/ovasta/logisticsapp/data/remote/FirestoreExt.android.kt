package com.ovasta.logisticsapp.data.remote

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.android

internal actual fun DocumentSnapshot.hasNonNull(field: String): Boolean = android.get(field) != null
