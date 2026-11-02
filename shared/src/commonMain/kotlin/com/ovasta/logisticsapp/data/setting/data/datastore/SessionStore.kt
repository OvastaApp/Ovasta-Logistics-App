package com.ovasta.logisticsapp.data.setting.data.datastore

import kotlinx.coroutines.flow.Flow

/**
 * The persisted session (user, token, settings). Android keeps it in an encrypted DataStore
 * file, iOS in a data-protected file; see createAndroidSessionStore / FileSessionStore.
 */
interface SessionStore {
    /** The current session, then every change to it. */
    val data: Flow<SessionPreferences>

    /** Atomically replaces the session with [transform] of the current one. */
    suspend fun update(transform: (SessionPreferences) -> SessionPreferences): SessionPreferences
}
