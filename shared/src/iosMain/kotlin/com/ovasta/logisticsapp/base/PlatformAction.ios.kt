package com.ovasta.logisticsapp.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSNotificationCenter

/** Posted on a language change; iosApp's ContentView recreates the Compose view for it. */
const val LANGUAGE_CHANGED_NOTIFICATION = "com.ovasta.logisticsapp.LanguageChanged"

@Composable
internal actual fun rememberPlatformActionPerformer(): (PlatformAction) -> Unit = remember {
    { action ->
        when (action) {
            PlatformAction.LanguageChanged ->
                NSNotificationCenter.defaultCenter.postNotificationName(LANGUAGE_CHANGED_NOTIFICATION, null)
        }
    }
}
