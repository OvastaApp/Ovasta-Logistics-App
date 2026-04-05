package com.ovasta.logisticsapp.presentation.orderDetails.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.lgSemiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropOfOrderDetailsScreen(
    viewModel: TaskDetailsViewModel, taskId: Int
) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    BaseScreen(
        viewModel = viewModel
    ) {
        OrderDetailsContent(
            onBackClick = { navigator.pop() }
        )

        LaunchedEffect(Unit) {
            viewModel.getTaskDetails(taskId = taskId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsContent(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.order_details),
                        style = lgSemiBold.copy(color = Base_white)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Base_white
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Primary
                )
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

