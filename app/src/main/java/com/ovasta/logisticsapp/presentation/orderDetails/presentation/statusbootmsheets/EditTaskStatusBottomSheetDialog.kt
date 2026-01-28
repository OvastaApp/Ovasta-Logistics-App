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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary50
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.Primary600
import com.ovasta.logisticsapp.base.ext.ToastHelper
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdSemiBold
import com.ovasta.logisticsapp.base.setOnClick
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.presentation.orderDetails.data.ReasonModel
import com.ovasta.logisticsapp.presentation.orderDetails.data.TaskStatusModel
import kotlinx.coroutines.launch
import kotlin.collections.forEachIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskStatusBottomSheetDialog(
    dialogState: StatusDialogState,
    onDismiss:()->Unit,
    onSelectTaskStatus:(TaskStatusModel)->Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = null
    ) {
        EditTaskStatusContent(
            dialogState = dialogState,
            sheetStata = sheetState,
            onDismiss=onDismiss,
            onSelectTaskStatus = onSelectTaskStatus
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskStatusContent(
    dialogState: StatusDialogState,
    sheetStata: SheetState,
    onDismiss: () -> Unit,
    onSelectTaskStatus:(TaskStatusModel)->Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedStatus: TaskStatusModel? by remember { mutableStateOf(dialogState.statusList.firstOrNull { it.isSelected }) }

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
                painter = painterResource(R.drawable.ic_edit_task),
                contentDescription = null,
                tint = Gray800
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                text = stringResource(R.string.edit_task),
                style = mdSemiBold
            )

            Icon(
                modifier = Modifier.setOnClick {
                    coroutineScope.launch { 
                        sheetStata.hide()
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

        dialogState.statusList.forEachIndexed { index, statusModel ->
            StatusItem(
                status = statusModel,
                isSelected = statusModel == selectedStatus,
            ) {
                selectedStatus = it
                it.isSelected = true
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
                    if(selectedStatus == null) {
                        ToastHelper.showShortToaster(context,
                            context.getString(R.string.please_select_option))
                        return@Button
                    }
                    onSelectTaskStatus(selectedStatus!!)
                },
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary500
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
private fun StatusItem(
    status: TaskStatusModel,
    isSelected: Boolean,
    onClick: (TaskStatusModel) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .setOnClick {
                onClick(status)
            }
            .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            .padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._12sdp))
            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
            .background(if (isSelected) Primary50 else Base_white)
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = if (isSelected) Primary500 else Gray200,
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
            )
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(status.icon),
            contentDescription = null,
            tint = status.textAndIconColor
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._18sdp)),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                modifier = Modifier.padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._2sdp)),
                text = stringResource(status.title),
                style = smMedium.copy(color = status.textAndIconColor)
            )

            Text(
                text = stringResource(status.description),
                style = smNormal.copy(color = Gray500)
            )

        }

        RadioButton(
            selected = isSelected,
            onClick = {
                onClick(status)
            },
            colors = RadioButtonDefaults
                .colors(
                    selectedColor = Primary600,
                    unselectedColor = Gray300
                )
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun EditTaskStatusContentPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.BottomCenter
    ) {
        EditTaskStatusContent(
            dialogState = StatusDialogState(
                reasonsList = listOf(
                    ReasonModel("1", "Reason 1"),
                )
            ),
            sheetStata = rememberModalBottomSheetState(),
            onSelectTaskStatus = {}
            , onDismiss = {}
        )
    }
}