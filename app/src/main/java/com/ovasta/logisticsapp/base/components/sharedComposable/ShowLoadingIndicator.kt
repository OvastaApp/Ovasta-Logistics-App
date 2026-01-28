package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.LightGrayBackground
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.Primary100

@Composable
fun ShowLoadingIndicator(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightGrayBackground.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag("loadingIndicator"),
            color = Primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingIndicator() {
    ShowLoadingIndicator(Modifier)
}