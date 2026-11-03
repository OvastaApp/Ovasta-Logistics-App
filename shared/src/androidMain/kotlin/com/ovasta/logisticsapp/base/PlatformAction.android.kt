package com.ovasta.logisticsapp.base

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberPlatformActionPerformer(): (PlatformAction) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { action ->
            when (action) {
                // Recreating the Activity reloads every screen in the new language and direction
                PlatformAction.LanguageChanged -> context.findActivity()?.recreate()
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
