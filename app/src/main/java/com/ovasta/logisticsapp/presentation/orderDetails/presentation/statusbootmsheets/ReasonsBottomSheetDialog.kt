package com.ovasta.logisticsapp.presentation.orderDetails.presentation.statusbootmsheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Error50
import com.ovasta.logisticsapp.base.Error500
import com.ovasta.logisticsapp.base.Error600
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary50
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.Primary600
import com.ovasta.logisticsapp.base.ext.ToastHelper
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.setOnClick
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smSemiBold
import com.ovasta.logisticsapp.presentation.orderDetails.data.ReasonModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReasonsBottomSheetDialog(
    dialogState: StatusDialogState,
    onDismiss:()->Unit,
    onBackToTaskStatus:()->Unit,
    onConfirm: (ReasonModel) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = null
    ) {
        ReasonsBottomSheetContent(
            dialogState = dialogState,
            sheetStata = sheetState,
            onDismiss = onDismiss,
            onBackToTaskStatus=onBackToTaskStatus,
            onConfirm = onConfirm
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReasonsBottomSheetContent(
    dialogState: StatusDialogState,
    sheetStata: SheetState,
    onDismiss: () -> Unit,
    onBackToTaskStatus:()->Unit,
    onConfirm: (ReasonModel) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedReason: ReasonModel? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Base_white)
    ) {

        Row(
            modifier = Modifier
                .padding(
                    top = dimensionResource(com.intuit.sdp.R.dimen._22sdp),
                    bottom = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                )
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.setOnClick {
                    coroutineScope.launch {
                        sheetStata.hide()
                        //onAction(DropOfOrderDetailsAction.BackToTaskStatus)
                        onBackToTaskStatus()
                    }
                },
                painter = painterResource(R.drawable.arrow_narrow_left),
                contentDescription = null,
                tint = Gray800
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                text =  if(dialogState.isRequestDelay) stringResource(R.string.delay_task) else stringResource(R.string.cancel_order),
                style = mdSemiBold
            )

            Icon(
                modifier = Modifier.setOnClick {
                    coroutineScope.launch {
                        sheetStata.hide()
                        //onAction(DropOfOrderDetailsAction.DismissStatusDialogs)
                        onDismiss()
                    }
                },
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
                tint = Gray800
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            thickness = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
            color = Gray200
        )

        Text(
            modifier = Modifier.padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._12sdp))
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
            text = stringResource(R.string.reason_for_delay),
            style = smSemiBold
        )

        dialogState.reasonsList.forEachIndexed { index, reason ->
            ReasonItem(
                reason = reason,
                isRequestDelay = dialogState.isRequestDelay,
                isSelected = selectedReason?.id == reason.id,
            ) {
                selectedReason = it
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Base_white)
                .border(width = dimensionResource(com.intuit.sdp.R.dimen._1sdp), color = Gray200)
                .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp),)
        ) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if(selectedReason == null) {
                        ToastHelper.showShortToaster(context,
                            context.getString(R.string.please_select_option))
                        return@Button
                    }
                    onDismiss()
                    onConfirm(selectedReason!!)
                },
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(dialogState.isRequestDelay) Primary500 else Error600
                )
            ) {
                Text(
                    text = stringResource(R.string.confirm),
                    style = mdMedium.copy(color = Base_white),
                )
            }

        }
    }
}

@Composable
private fun ReasonItem(
    reason: ReasonModel,
    isRequestDelay: Boolean,
    isSelected: Boolean,
    onClick: (ReasonModel) -> Unit
) {
    Row(
        modifier = Modifier
            .setOnClick { onClick(reason) }
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            .padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._12sdp))
            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
            .background(if (isSelected) (if (isRequestDelay) Primary50 else Error50) else Base_white)
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = if (isSelected) (if (isRequestDelay) Primary500 else Error500) else Gray200,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(com.intuit.sdp.R.dimen._8sdp), bottom = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
            text = reason.value,
            style = smMedium.copy(color = Gray800)
        )

        RadioButton(
            selected = isSelected,
            onClick = {
                onClick(reason)
            },
            colors = RadioButtonDefaults
                .colors(
                    selectedColor = if(isRequestDelay) Primary600 else Error600,
                    unselectedColor = Gray300
                )
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true, locale = "ar")
private fun ReasonsBottomSheetContentPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.BottomCenter
    ) {
        ReasonsBottomSheetContent(
            dialogState = StatusDialogState(
                reasonsList = listOf(
                    ReasonModel("1", "Reason 1"),
                ),
                isRequestDelay = true
            ),
            sheetStata = rememberModalBottomSheetState(),
            onDismiss = {},
            onBackToTaskStatus = {},
            onConfirm = {}
        )
    }
}