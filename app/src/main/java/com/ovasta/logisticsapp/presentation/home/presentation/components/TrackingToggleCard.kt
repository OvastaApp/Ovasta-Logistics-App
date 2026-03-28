package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.smMedium


@Composable
fun TrackingToggleCard(
    isTracking: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isTracking) Color(0xFFE8F5E9) else Color(0xFFFFF5F5),
        animationSpec = tween(300),
        label = "trackingBg"
    )
    val accentColor by animateColorAsState(
        targetValue = if (isTracking) Color(0xFF2E7D32) else Color(0xFFB71C1C),
        animationSpec = tween(300),
        label = "trackingAccent"
    )

    Card(
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._10sdp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = accentColor.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._4sdp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Animated status dot
                Box(
                    modifier = Modifier
                        .size(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                        .background(accentColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                Text(
                    text = if (isTracking) stringResource(R.string.tracking_active)
                    else stringResource(R.string.tracking_inactive),
                    style = smMedium.copy(color = accentColor)
                )
            }

            Switch(
                checked = isTracking,
                onCheckedChange = { onToggle() },
                modifier = Modifier.scale(0.85f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFD0D0D0),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackingToggleCardPreview() {
    TrackingToggleCard(isTracking = true, onToggle = {})
}
