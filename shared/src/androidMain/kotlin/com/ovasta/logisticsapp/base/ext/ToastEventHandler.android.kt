package com.ovasta.logisticsapp.base.ext

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberPlatformToaster(): (String) -> Unit {
    val context = LocalContext.current
    return remember(context) { { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() } }
}
