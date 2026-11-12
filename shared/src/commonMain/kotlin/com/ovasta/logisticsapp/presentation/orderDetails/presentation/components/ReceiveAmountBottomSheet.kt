package com.ovasta.logisticsapp.presentation.orderDetails.presentation.components

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.cancel
import com.ovasta.logisticsapp.shared.resources.confirm_collect_money
import com.ovasta.logisticsapp.shared.resources.deliver_order
import com.ovasta.logisticsapp.shared.resources.price_currency
import com.ovasta.logisticsapp.shared.resources.received_amount
import com.ovasta.logisticsapp.shared.resources.total_price

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
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.lgSemiBold
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.base.ext.sdp

/**
 * Bottom sheet shown when delivering an order: the agent enters the cash amount collected from
 * the client. [totalAmount] is shown for reference only — the collected amount may be less or
 * more than it; Confirm is enabled once any amount is entered.
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
    val isValid = parsedAmount != null

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
                .padding(horizontal = 16.sdp)
                .padding(bottom = 16.sdp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.deliver_order),
                style = lgMedium.copy(color = Gray900),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.sdp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.total_price),
                    style = smNormal.copy(color = Gray800)
                )
                Text(
                    text = stringResource(Res.string.price_currency, formatAmount(totalAmount)),
                    style = lgSemiBold.copy(color = Primary)
                )
            }

            Spacer(modifier = Modifier.height(12.sdp))

            OutlinedTextField(
                value = amountText,
                textStyle = mdMedium,
                onValueChange = { input -> if (isValidAmountInput(input)) amountText = input },
                label = { Text(stringResource(Res.string.received_amount), style = xsMedium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.sdp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.sdp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.sdp),
                ) {
                    Text(
                        text = stringResource(Res.string.cancel),
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
                    shape = RoundedCornerShape(8.sdp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Base_white,
                        disabledContainerColor = Primary.copy(alpha = 0.4f),
                        disabledContentColor = Base_white.copy(alpha = 0.7f)
                    ),
                ) {
                    Text(
                        text = stringResource(Res.string.confirm_collect_money),
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
