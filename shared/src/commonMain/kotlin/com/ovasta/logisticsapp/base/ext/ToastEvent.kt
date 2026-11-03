package com.ovasta.logisticsapp.base.ext

import org.jetbrains.compose.resources.StringResource

sealed class ToastEvent {
    class StringToastEvent(
        val message: String,
    ) : ToastEvent()

    class ResourceToastEvent(
        val resource: StringResource,
        vararg val args: Any,
    ) : ToastEvent()
}
