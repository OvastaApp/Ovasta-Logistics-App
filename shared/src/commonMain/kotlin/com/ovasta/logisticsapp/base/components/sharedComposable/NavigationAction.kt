package com.ovasta.logisticsapp.base.components.sharedComposable

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.contact
import com.ovasta.logisticsapp.shared.resources.directions
import com.ovasta.logisticsapp.shared.resources.ic_call
import com.ovasta.logisticsapp.shared.resources.ic_pick_pin
import com.ovasta.logisticsapp.shared.resources.ic_whatsapp
import com.ovasta.logisticsapp.shared.resources.whatsapp

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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray700
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.ext.sdp


@Composable
fun NavigationAction(
    clickedTaskId: Int,
    onDirectionClick: () -> Unit,
    onContactClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.sdp)
    ) {
        ContactButton(
            iconRes = Res.drawable.ic_pick_pin,
            textRes = Res.string.directions,
            modifier = Modifier
                .weight(1f)
                .testTag("directionsButton_${clickedTaskId}"),
            onClick = onDirectionClick
        )
        ContactButton(
            iconRes = Res.drawable.ic_call,
            textRes = Res.string.contact,
            modifier = Modifier
                .weight(1f)
                .testTag("contactButton_${clickedTaskId}"),
            onClick = onContactClick
        )
        ContactButton(
            iconRes = Res.drawable.ic_whatsapp,
            textRes = Res.string.whatsapp,
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
    iconRes: DrawableResource,
    textRes: StringResource,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.sdp),
        color = Base_white,
        border = BorderStroke(1.sdp, Gray300),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 6.sdp,
                vertical = 10.sdp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(textRes),
                modifier = Modifier.size(16.sdp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(4.sdp))
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
fun ContactButton(textRes: StringResource, iconRes: DrawableResource, modifier: Modifier, onClick: () -> Unit) {
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