package com.ovasta.logisticsapp.presentation.orderDetails.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Error500
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium

/**
 * Bottom sheet shown when delivering an order: the agent enters the cash amount collected from
 * the client. Confirm is enabled only when the amount is at least [totalAmount] — collecting
 * less than the order total is not allowed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveAmountBottomSheet(
    totalAmount: Double,
    onConfirm: (receivedAmount: Double) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amountText by remember { mutableStateOf("") }
    val parsedAmount = amountText.toDoubleOrNull()

    val isTooLow = parsedAmount != null && parsedAmount < totalAmount
    val isValid = parsedAmount != null && parsedAmount >= totalAmount

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Base_white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp))
                .padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._16sdp))
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.deliver_order),
                style = lgMedium.copy(color = Gray900),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total_price),
                    style = smNormal.copy(color = Gray800)
                )
                Text(
                    text = stringResource(R.string.price_currency, formatAmount(totalAmount)),
                    style = lgSemiBold.copy(color = Primary)
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

            OutlinedTextField(
                value = amountText,
                textStyle = mdMedium,
                onValueChange = { input -> if (isValidAmountInput(input)) amountText = input },
                label = { Text(stringResource(R.string.received_amount), style = xsMedium) },
                singleLine = true,
                isError = isTooLow,
                supportingText = {
                    if (isTooLow) {
                        Text(
                            text = stringResource(R.string.received_less_than_total),
                            style = xsMedium.copy(color = Error500)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss,
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = xsMedium.copy(color = Primary)
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),
                    enabled = isValid,
                    onClick = {
                        val amount = parsedAmount ?: return@Button
                        onConfirm(amount)
                    },
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Base_white,
                        disabledContainerColor = Primary.copy(alpha = 0.4f),
                        disabledContentColor = Base_white.copy(alpha = 0.7f)
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.confirm_collect_money),
                        style = xsMedium.copy(
                            color = if (isValid) Base_white else Base_white.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

/** Accepts an empty string or up to 7 digits with an optional 2-decimal fraction. */
private fun isValidAmountInput(input: String): Boolean {
    if (input.isEmpty()) return true
    return input.matches(Regex("^\\d{0,7}(\\.\\d{0,2})?$"))
}

/** Formats an amount without a trailing ".0" for whole numbers (e.g. 250.0 -> "250"). */
private fun formatAmount(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

@Preview(showBackground = true, locale = "ar")
@Composable
private fun ReceiveAmountBottomSheetPreview() {
    ReceiveAmountBottomSheet(
        totalAmount = 265.0,
        onConfirm = {},
        onDismiss = {}
    )
}
