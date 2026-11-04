package com.ovasta.logisticsapp.data.remote

import dev.gitlive.firebase.firestore.DocumentSnapshot

/**
 * Whether the document has [field] with a non-null value of any type, like the native
 * `snapshot.get(field) != null`. GitLive's `contains` is also true for a stored null.
 */
internal expect fun DocumentSnapshot.hasNonNull(field: String): Boolean
