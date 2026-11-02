package com.ovasta.logisticsapp.presentation.auth.login.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.ext.sdp

@Composable
fun UserTypeOption(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.sdp))
            .background(if (isSelected) Primary.copy(alpha = 0.1f) else Base_white)
            .border(
                width = 1.sdp,
                color = if (isSelected) Primary else Gray200,
                shape = RoundedCornerShape(8.sdp)
            )
            .clickable { onClick() }
            .padding(12.sdp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(32.sdp),
            tint = if (isSelected) Primary else Gray800
        )
        Text(
            text = label,
            style = smMedium.copy(
                color = if (isSelected) Primary else Gray800
            ),
            modifier = Modifier.padding(top = 4.sdp)
        )
    }
}

