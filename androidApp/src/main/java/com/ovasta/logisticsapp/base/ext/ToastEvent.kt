package com.ovasta.logisticsapp.base.ext

import android.widget.Toast
import androidx.annotation.StringRes

sealed class ToastEvent {

    class StringToastEvent(
        val message: String,
        val duration: Int = Toast.LENGTH_SHORT
    ) : ToastEvent()

    class ResourceToastEvent(
        @StringRes val resId: Int,
        vararg val args: Any,
        val duration: Int = Toast.LENGTH_SHORT
    ) : ToastEvent()
}