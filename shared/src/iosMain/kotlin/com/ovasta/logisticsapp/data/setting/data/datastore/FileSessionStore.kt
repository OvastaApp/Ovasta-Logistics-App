package com.ovasta.logisticsapp.data.setting.data.datastore

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileProtectionCompleteUntilFirstUserAuthentication
import platform.Foundation.NSFileProtectionKey
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

/**
 * The session as JSON in Application Support. iOS encrypts the file at rest; it is readable
 * after the first unlock, so background location tracking can still send the token while the
 * phone is locked. It is deleted with the app, unlike a Keychain item.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class FileSessionStore(private val path: String = defaultPath()) : SessionStore {

    private val mutex = Mutex()
    private val state = MutableStateFlow(read())

    override val data: Flow<SessionPreferences> = state.asStateFlow()

    override suspend fun update(transform: (SessionPreferences) -> SessionPreferences) =
        mutex.withLock {
            val updated = transform(state.value)
            write(updated)
            state.value = updated
            updated
        }

    private fun read(): SessionPreferences {
        val json = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null)
            ?: return SessionPreferences()
        return runCatching { SessionJson.decode(json) }.getOrDefault(SessionPreferences())
    }

    private fun write(session: SessionPreferences) {
        NSString.create(string = SessionJson.encode(session))
            .writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
        NSFileManager.defaultManager.setAttributes(
            mapOf(NSFileProtectionKey to NSFileProtectionCompleteUntilFirstUserAuthentication),
            ofItemAtPath = path,
            error = null
        )
    }

    private companion object {
        fun defaultPath(): String {
            val directory = NSSearchPathForDirectoriesInDomains(
                NSApplicationSupportDirectory, NSUserDomainMask, true
            ).first() as String
            NSFileManager.defaultManager.createDirectoryAtPath(
                directory, withIntermediateDirectories = true, attributes = null, error = null
            )
            return "$directory/session.json"
        }
    }
}
