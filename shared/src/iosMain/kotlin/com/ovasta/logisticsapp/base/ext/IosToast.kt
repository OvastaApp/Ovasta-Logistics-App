package com.ovasta.logisticsapp.base.ext

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ovasta.logisticsapp.base.components.sharedComposable.ToastMsg
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

// Android's Toast.LENGTH_SHORT
private const val TOAST_DURATION_MS = 2_000L

// Material 3's NavigationBar (AppBottomNavigation), without the safe area under it
private val BOTTOM_BAR_HEIGHT = 80.dp

/** The toast iOS doesn't have: [show] queues a message and [IosToastHost] shows it. */
internal object IosToaster {
    // Unlimited, so toasts are shown one after another, in order, like on Android
    val messages = Channel<String>(Channel.UNLIMITED)

    fun show(message: String) {
        messages.trySend(message)
    }
}

/**
 * Shows [IosToaster]'s messages one at a time, at the bottom of the screen, as [ToastMsg].
 * Put it once, over the whole app.
 */
@Composable
internal fun IosToastHost() {
    var text by remember { mutableStateOf("") }
    var toastNumber by remember { mutableIntStateOf(0) }
    val visibility = remember { MutableTransitionState(false) }

    LaunchedEffect(Unit) {
        for (message in IosToaster.messages) {
            text = message
            toastNumber++
            visibility.targetState = true
            delay(TOAST_DURATION_MS)
            visibility.targetState = false
            // Let the fade-out finish before the next toast
            snapshotFlow { visibility.isIdle && !visibility.currentState }.first { it }
        }
    }

    if (visibility.currentState || visibility.targetState) {
        // Where Home shows its own toast: 60.sdp above the bottom bar, which sits on the safe
        // area. Above the keyboard while it's open.
        val bottomOffset = with(LocalDensity.current) {
            WindowInsets.safeDrawing.getBottom(this) + (BOTTOM_BAR_HEIGHT + 60.sdp).roundToPx()
        }
        val sideMargins = 120.sdp
        // A new popup for every toast: its layer is created above any dialog or sheet open by
        // then. Queued toasts would otherwise reuse a layer from before the dialog, below it.
        key(toastNumber) {
            Popup(
                alignment = Alignment.BottomCenter,
                offset = IntOffset(0, -bottomOffset),
                properties = PopupProperties(focusable = false),
            ) {
                // A max width instead of side padding: transparent strips beside the toast would
                // take the taps meant for the screen
                BoxWithConstraints {
                    Box(Modifier.widthIn(max = maxWidth - sideMargins)) {
                        AnimatedVisibility(visibleState = visibility, enter = fadeIn(), exit = fadeOut()) {
                            ToastMsg(text = text)
                        }
                    }
                }
            }
        }
    }
}
