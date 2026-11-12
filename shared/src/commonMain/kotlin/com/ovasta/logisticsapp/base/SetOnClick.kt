package com.ovasta.logisticsapp.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier

inline fun Modifier.setOnClick(isEnable: Boolean = true, noinline onClick: () -> Unit): Modifier {
    return this.clickable(
        onClick = onClick,
        indication = null,
        interactionSource = MutableInteractionSource(),
        enabled = isEnable
    )
}