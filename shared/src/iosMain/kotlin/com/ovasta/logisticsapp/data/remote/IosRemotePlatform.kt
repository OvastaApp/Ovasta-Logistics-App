package com.ovasta.logisticsapp.data.remote

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.generic_unknown_error
import com.ovasta.logisticsapp.shared.resources.timeout_error_message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.concurrent.Volatile

/**
 * The FCM token. Only the Swift AppDelegate can read it (Messaging delegate), so it sets it
 * here: `PushTokenBridge.shared.token = fcmToken`.
 */
object PushTokenBridge {
    @Volatile
    var token: String? = null
}

/**
 * The error texts from the shared strings. getString is suspend and the HTTP client reads them
 * synchronously, so they are loaded ahead: at start and whenever the UI is rebuilt for a new
 * language. Until then the English texts are used.
 */
class IosNetworkErrorMessages : NetworkErrorMessages {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var timeoutText = "An error has occurred, please try again later"

    @Volatile
    private var unknownText = "An unknown error has occurred"

    fun reload() {
        scope.launch {
            timeoutText = getString(Res.string.timeout_error_message)
            unknownText = getString(Res.string.generic_unknown_error)
        }
    }

    override fun timeout(): String = timeoutText

    override fun genericUnknown(): String = unknownText
}
