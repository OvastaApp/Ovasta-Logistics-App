package com.ovasta.logisticsapp.presentation.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.mdRegular

// Page opened from a profile item, showing only an empty message until its content is built
@Composable
fun ProfileDetailsPlaceholder(
    title: String,
    message: String,
    onBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = title,
                onBackButtonPressed = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Gray100)
                .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = mdRegular,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProfileDetailsPlaceholderPreview() {
    ProfileDetailsPlaceholder(
        title = "Wallet",
        message = "No wallet transactions yet",
        onBack = {}
    )
}
