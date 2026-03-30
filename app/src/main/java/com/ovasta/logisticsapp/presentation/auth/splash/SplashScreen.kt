package com.ovasta.logisticsapp.presentation.auth.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.ScreenDirectionEventHandler
import com.ovasta.logisticsapp.base.White

@Composable
fun SplashScreen(
    viewModel: SplashViewModel
) {

    ScreenDirectionEventHandler(
        viewModel = viewModel
    )


    Box(modifier = Modifier.fillMaxSize()) {
        SplashContent()
    }
}

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(250.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SplashPreview() {
    SplashContent()
}