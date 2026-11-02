package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.ext.sdp


@Composable
fun TrackingToggleCard(
    isTracking: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor = if (isTracking) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val accentColor = if (isTracking) Color(0xFF2E7D32) else Color(0xFFB71C1C)

    Card(
        shape = RoundedCornerShape(8.sdp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = 1.sdp,
            color = accentColor.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.sdp,
                    vertical = 6.sdp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isTracking) R.drawable.ic_start_tracking else R.drawable.ic_stop_tracking
                    ),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.sdp)
                )
                Spacer(modifier = Modifier.width(8.sdp))
                Text(
                    text = if (isTracking) stringResource(R.string.tracking_active)
                    else stringResource(R.string.tracking_inactive),
                    style = smNormal.copy(color = accentColor)
                )
            }

            androidx.compose.material3.Switch(
                checked = isTracking,
                onCheckedChange = { onToggle() },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                    uncheckedBorderColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

