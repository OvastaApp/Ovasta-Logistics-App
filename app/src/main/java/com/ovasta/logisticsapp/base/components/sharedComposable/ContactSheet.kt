package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.Primary50
import com.ovasta.logisticsapp.base.Primary70
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.HomeTask
import com.ovasta.logisticsapp.presentation.home.presentation.HomeItemActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSheet(
    taskId: Int,
    homeTaskInfo: HomeTask,
    onAction: (HomeItemActions) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = {
            onAction(HomeItemActions.DismissContactBottomSheet)
        },
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.testTag("contactSheet")
    ) {
        ContactSheetContent(taskId, homeTaskInfo, onAction)
    }
}

@Composable
fun ContactSheetContent(
    visitId: Int,
    homeTaskInfo: HomeTask,
    onAction: (HomeItemActions) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                    topEnd = dimensionResource(com.intuit.sdp.R.dimen._12sdp)
                )
            )
            .background(Base_white)
            .padding(
                top = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
            )
    ) {

        SheetMarketInfo(
            visitId = visitId,
            marketName = homeTaskInfo.customerName ?: "",
            marketAddress = homeTaskInfo.customerAddress ?: "",
            onAction
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                    end = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                    start = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                )
        ) {
            CallButton(
                Modifier.weight(1f),
                phone = homeTaskInfo.clientPhone ?: "",
                onClick = {
                    onAction(
                        HomeItemActions.CallRetailer(
                            homeTaskInfo.clientPhone ?: ""
                        )
                    )
                })

            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
            CopyButton(
                onClick = {
                    onAction(HomeItemActions.CopyPhone(homeTaskInfo.clientPhone ?: ""))
                })

        }

        WhatsAppButton(
            phoneNumber = homeTaskInfo.clientPhone ?: "",
            onClick = {
                onAction(
                    HomeItemActions.WhatsAppRetailer(
                        homeTaskInfo.clientPhone ?: ""
                    )
                )
            })

        // Add bottom padding for navigation bar
        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._8sdp))
        )
    }
}

@Composable
fun SheetMarketInfo(
    visitId: Int,
    marketName: String,
    marketAddress: String,
    onAction: (HomeItemActions) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
            end = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_market_24),
            contentDescription = "market",
            modifier = Modifier
        )
        Column(modifier = Modifier.weight(1f)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = marketName,
                    style = mdSemiBold,
                    modifier = Modifier.padding(start = dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                )
                Text(
                    text = String.format(stringResource(R.string.hash_task), visitId),
                    style = xsMedium,
                    modifier = Modifier.padding(
                        start = dimensionResource(com.intuit.sdp.R.dimen._4sdp),
                        top = dimensionResource(com.intuit.sdp.R.dimen._3sdp)
                    )
                )
            }

            Text(
                text = marketAddress.ifEmpty { stringResource(R.string.no_address) },
                style = smMedium.copy(color = Gray500),
                modifier = Modifier
                    .padding(
                        top = dimensionResource(com.intuit.sdp.R.dimen._2sdp),
                        start = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                        end = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                    )
            )
        }

        IconButton(onClick = { onAction(HomeItemActions.DismissContactBottomSheet) }) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "close",
                modifier = Modifier
            )
        }
    }
}

@Composable
fun CallButton(modifier: Modifier, phone: String, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = Gray200,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            )
            .testTag("callRetailerButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._14sdp)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_call),
                contentDescription = "call",
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            Text(
                text = stringResource(R.string.call),
                style = smMedium.copy(color = Gray800)
            )
            Spacer(
                modifier = Modifier
                    .width(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                    .weight(1f)
            )

            Text(
                text = phone,
                style = smMedium.copy(color = Primary),
            )
        }
    }
}

@Composable
fun CopyButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .testTag("copyPhoneButton")
            .background(
                Primary50,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            )) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._18sdp)
            )
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_copy),
                contentDescription = "copy",
                tint = Primary70,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            Text(
                text = stringResource(R.string.copy),
                style = mdMedium.copy(color = Primary70)
            )
        }
    }
}

@Composable
fun WhatsAppButton(phoneNumber: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            .clickable { onClick() }
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = Gray200,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            )
            .testTag("whatsappRetailerButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                vertical = dimensionResource(com.intuit.sdp.R.dimen._12sdp),
                horizontal = dimensionResource(com.intuit.sdp.R.dimen._14sdp)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_whatsapp),
                contentDescription = "call",
                tint = Color.Unspecified,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
            Text(
                text = stringResource(R.string.message_on_whatsapp),
                style = smMedium.copy(color = Gray800)
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

            Text(
                text = phoneNumber,
                style = smMedium.copy(color = Primary70, textAlign = TextAlign.End),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
@Preview(showBackground = true, locale = "ar")
fun PreviewContactSheet() {
    val homeTaskInfo = HomeTask()
    ContactSheetContent(
        12, homeTaskInfo
    ) {}
}