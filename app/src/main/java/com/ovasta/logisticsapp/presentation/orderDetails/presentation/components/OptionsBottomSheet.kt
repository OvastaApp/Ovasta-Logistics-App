package com.ovasta.logisticsapp.presentation.orderDetails.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium

/** One tappable row in an [OptionsBottomSheet]. */
data class SheetOption(
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val onClick: () -> Unit,
)

/**
 * Bottom sheet with a title and a list of tappable options, e.g. the order's edit options
 * (request edit / upload receipt) or the image source (camera / gallery).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsBottomSheet(
    @StringRes title: Int,
    options: List<SheetOption>,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Base_white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp))
                .padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(title),
                style = lgMedium.copy(color = Gray900),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            options.forEach { option -> OptionRow(option) }
        }
    }
}

@Composable
private fun OptionRow(option: SheetOption) {
    val shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = Gray200,
                shape = shape
            )
            .clickable(onClick = option.onClick)
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(option.icon),
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._20sdp))
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
        Text(
            text = stringResource(option.label),
            style = mdMedium.copy(color = Gray900),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun OptionsBottomSheetPreview() {
    OptionsBottomSheet(
        title = R.string.edit_order,
        options = listOf(
            SheetOption(R.drawable.ic_edit_task, R.string.request_edit) {},
            SheetOption(R.drawable.ic_image, R.string.upload_receipt) {},
        ),
        onDismiss = {}
    )
}
