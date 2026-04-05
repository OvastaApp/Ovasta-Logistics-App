package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator

@Composable
fun DropOfOrderDetailsScreen(
    viewModel: TaskDetailsViewModel, taskId: Int
) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    // Handle system back button/gesture - placed first to intercept early
    BackHandler(enabled = true, onBack = {
        navigator.pop()
    })

    LaunchedEffect(Unit) {
        viewModel.getTaskDetails(taskId = taskId)
    }

    BaseScreen(
        viewModel = viewModel
    ) {
        OrderDetailsContent(
            onBackClick = { navigator.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsContent(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.order_details),
                showBackButton = true,
                onBackButtonPressed = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TODO: Add order details content here
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderDetailsContentPreview() {
    OrderDetailsContent()
}

