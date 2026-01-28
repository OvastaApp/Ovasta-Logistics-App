package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray700
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.smMedium


@Composable
fun NavigationAction(
    clickedTaskId:Int,
    startedTaskId: Int,
    onTaskDetailsClick: () -> Unit,
    onDirectionClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    ) {
        val isStarted = startedTaskId != 0 && clickedTaskId == startedTaskId
        if (isStarted) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(com.intuit.sdp.R.dimen._40sdp))
                    .testTag("detailsButton_${startedTaskId}"),
                onClick = { onTaskDetailsClick() },
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary500
                ),
            ) {
                Text(
                    text = stringResource(
                        R.string.continue_the_task
                    ),
                    style = smMedium.copy(color = Base_white)
                )
            }
        } else {
            DirectionButton(modifier = Modifier.weight(1f).testTag("directionsButton_${clickedTaskId}")
                , onDirectionClick)
            ContactButton(modifier = Modifier.weight(1f).testTag("contactButton_${clickedTaskId}"), onContactClick)
        }
    }
}




@Composable
fun DirectionButton(modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier.clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        color = Base_white,
        border = BorderStroke(dimensionResource(com.intuit.sdp.R.dimen._1sdp), Gray300),
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp)
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {
            Icon(
                painter = painterResource(R.drawable.ic_pick_pin),
                contentDescription = stringResource(R.string.directions),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            Text(
                text = stringResource(R.string.directions), style = smMedium.copy(color = Gray700)
            )

        }
    }
}

@Composable
fun ContactButton(modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        color = Base_white,
        border = BorderStroke(dimensionResource(com.intuit.sdp.R.dimen._1sdp), Gray300),
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp)
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {
            Icon(
                painter = painterResource(R.drawable.ic_call),
                contentDescription = stringResource(R.string.contact),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            Text(
                text = stringResource(R.string.contact), style = smMedium.copy(color = Gray700)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskNavigation(){
  NavigationAction(3,3,{},{}) { }
}
@Preview(showBackground = true)
@Composable
fun PreviewStartedTaskNavigation(){
    NavigationAction(3,1222,{},{}) { }
}