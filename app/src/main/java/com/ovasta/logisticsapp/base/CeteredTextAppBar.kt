package com.ovasta.logisticsapp.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ovasta.logisticsapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTextAppBar(
    title: String,
    showBackButton: Boolean = true,
    onBackButtonPressed: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = Modifier, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Base_white
        ),
        title = {
            Text(
                text = title,
                style = mdSemiBold,
                color = Color.Black
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackButtonPressed) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_narrow_left),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        },
        actions = actions
    )
}
