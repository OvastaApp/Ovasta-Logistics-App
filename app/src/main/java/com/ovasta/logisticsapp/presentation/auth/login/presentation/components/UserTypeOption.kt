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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.smMedium

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
            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
            .background(if (isSelected) Primary.copy(alpha = 0.1f) else Base_white)
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = if (isSelected) Primary else Gray200,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            )
            .clickable { onClick() }
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._32sdp)),
            tint = if (isSelected) Primary else Gray800
        )
        Text(
            text = label,
            style = smMedium.copy(
                color = if (isSelected) Primary else Gray800
            ),
            modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._4sdp))
        )
    }
}

