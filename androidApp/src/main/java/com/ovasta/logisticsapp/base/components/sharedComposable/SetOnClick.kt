package com.ovasta.logisticsapp.base.components.sharedComposable

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import com.ovasta.logisticsapp.base.constants.LocalConstants.DELAY_CLICK_ACTION

fun Modifier.setOnClick(isEnable: Boolean = true, onClick: () -> Unit): Modifier {
    return this.clickable(
        onClick = onClick,
        indication = null,
        interactionSource = MutableInteractionSource(),
        enabled = isEnable
    )
}

fun Modifier.safeClickable(
    interval: Long = DELAY_CLICK_ACTION,
    onClick: () -> Unit
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "safeClickable"
        properties["interval"] = interval
    }
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }

    clickable {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime.longValue >= interval) {
            lastClickTime.longValue = currentTime
            onClick()
        }
    }
}

@Composable
fun rememberSafeClick(
    interval: Long = DELAY_CLICK_ACTION,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    return {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime
            onClick()
        }
    }
}