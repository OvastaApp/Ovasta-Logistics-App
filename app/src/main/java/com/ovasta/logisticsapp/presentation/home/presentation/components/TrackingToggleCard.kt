package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal


@Composable
fun TrackingToggleCard(
    isTracking: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor = if (isTracking) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val accentColor = if (isTracking) Color(0xFF2E7D32) else Color(0xFFB71C1C)

    Card(
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = accentColor.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
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
                    modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._24sdp))
                )
                Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
                Column {
                    Text(
                        text = if (isTracking) stringResource(R.string.tracking_active)
                        else stringResource(R.string.tracking_inactive),
                        style = mdMedium.copy(color = accentColor)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isTracking) stringResource(R.string.stop_work)
                        else stringResource(R.string.start_work),
                        style = smNormal.copy(color = Gray500)
                    )
                }
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