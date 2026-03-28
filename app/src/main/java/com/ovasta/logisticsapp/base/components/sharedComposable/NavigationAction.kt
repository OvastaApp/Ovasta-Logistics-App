package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray700
import com.ovasta.logisticsapp.base.smMedium


@Composable
fun NavigationAction(
    clickedTaskId: Int,
    onDirectionClick: () -> Unit,
    onContactClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    ) {
        ContactButton(
            iconRes = R.drawable.ic_pick_pin,
            textRes = R.string.directions,
            modifier = Modifier
                .weight(1f)
                .testTag("directionsButton_${clickedTaskId}"),
            onClick = onDirectionClick
        )
        ContactButton(
            iconRes = R.drawable.ic_call,
            textRes = R.string.contact,
            modifier = Modifier
                .weight(1f)
                .testTag("contactButton_${clickedTaskId}"),
            onClick = onContactClick
        )
        ContactButton(
            iconRes = R.drawable.ic_whatsapp,
            textRes = R.string.whatsapp,
            modifier = Modifier
                .weight(1f)
                .testTag("contactButton_${clickedTaskId}"),
            onClick = onWhatsAppClick
        )
    }
}


@Composable
fun ActionButton(
    modifier: Modifier,
    iconRes: Int,
    textRes: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        color = Base_white,
        border = BorderStroke(dimensionResource(com.intuit.sdp.R.dimen._1sdp), Gray300),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._6sdp),
                vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp)
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(textRes),
                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))
            Text(
                text = stringResource(textRes),
                style = smMedium.copy(color = Gray700),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun ContactButton(textRes: Int, iconRes: Int, modifier: Modifier, onClick: () -> Unit) {
    ActionButton(
        modifier = modifier,
        iconRes = iconRes,
        textRes = textRes,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStartedTaskNavigation() {
    NavigationAction(3, {}, {}, {})
}