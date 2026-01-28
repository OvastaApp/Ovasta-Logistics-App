package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.ui.theme.Purple80

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)) // dim background
            .noClickThrough(), // <- custom extension to block clicks
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Purple80)
    }
}

fun Modifier.noClickThrough() = this.then(
    Modifier
        .background(Color.Transparent) // needed for click area
        .pointerInput(Unit) {}         // consumes all clicks
)