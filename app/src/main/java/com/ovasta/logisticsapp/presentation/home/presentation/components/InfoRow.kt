package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.ext.sdp

@Composable
fun InfoRow(
    icon: Int,
    label: String,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = smNormal.copy(color = Gray500),
    iconSize: Dp = 20.sdp, // uniform size for all icons
    iconTint: Color = Gray500
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(iconSize),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.width(4.sdp))
        Text(
            text = label,
            style = textStyle
        )
    }
}


@Preview
@Composable
fun InfoRowPreview() {
    InfoRow(
        icon = com.ovasta.logisticsapp.R.drawable.ic_location,
        label = "123 Main St, Springfield"
    )
}