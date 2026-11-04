package com.ovasta.logisticsapp.presentation.home.presentation.components

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.cancel
import com.ovasta.logisticsapp.shared.resources.create
import com.ovasta.logisticsapp.shared.resources.create_delivery_order_subtitle
import com.ovasta.logisticsapp.shared.resources.create_delivery_order_title
import com.ovasta.logisticsapp.shared.resources.delivery_price_hint
import com.ovasta.logisticsapp.shared.resources.ic_delivery_truck
import com.ovasta.logisticsapp.shared.resources.note_hint
import com.ovasta.logisticsapp.shared.resources.receiver_mobile_error
import com.ovasta.logisticsapp.shared.resources.receiver_mobile_hint
import com.ovasta.logisticsapp.shared.resources.to_address_hint

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.base.ext.sdp

/**
 * Bottom sheet that collects the fields required to create a delivery order.
 *
 * Only the delivery price is required; receiver mobile, address and note are optional and are
 * passed through as `null` when left blank. The confirm button is enabled only when the parsed
 * delivery price is a non-negative value within [0, [maxPrice]].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeliveryOrderBottomSheet(
    maxPrice: Double = 9999.0,
    onConfirm: (deliveryPrice: Double, receiverMobile: String?, toAddress: String?, note: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var priceText by remember { mutableStateOf("") }
    var receiverMobile by remember { mutableStateOf("") }
    var toAddress by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val parsedPrice = priceText.toDoubleOrNull()
    val isPriceValid = parsedPrice != null && parsedPrice in 0.0..maxPrice
    // Receiver mobile is optional, but when provided it must be exactly 11 digits.
    val isMobileValid = receiverMobile.isEmpty() || receiverMobile.length == MOBILE_LENGTH
    val isValid = isPriceValid && isMobileValid

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
            Icon(
                painter = painterResource(Res.drawable.ic_delivery_truck),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(24.sdp)
            )

            Spacer(modifier = Modifier.height(12.sdp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.create_delivery_order_title),
                style = lgMedium.copy(color = Gray900),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.sdp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.create_delivery_order_subtitle),
                style = smNormal.copy(color = Gray500),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.sdp))

            OutlinedTextField(
                value = priceText,
                textStyle = mdMedium,
                onValueChange = { input -> if (isValidPriceInput(input)) priceText = input },
                label = { Text(stringResource(Res.string.delivery_price_hint), style = xsMedium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = receiverMobile,
                textStyle = mdMedium,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length <= MOBILE_LENGTH) {
                        receiverMobile = input
                    }
                },
                label = { Text(stringResource(Res.string.receiver_mobile_hint), style = xsMedium) },
                singleLine = true,
                isError = !isMobileValid,
                supportingText = if (!isMobileValid) {
                    { Text(stringResource(Res.string.receiver_mobile_error), style = xsMedium) }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = toAddress,
                textStyle = mdMedium,
                onValueChange = { toAddress = it },
                label = { Text(stringResource(Res.string.to_address_hint), style = xsMedium) },
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                textStyle = mdMedium,
                onValueChange = { note = it },
                label = { Text(stringResource(Res.string.note_hint), style = xsMedium) },
                singleLine = false,
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
                        parsedPrice?.let {
                            onConfirm(
                                it,
                                receiverMobile.trim().ifBlank { null },
                                toAddress.trim().ifBlank { null },
                                note.trim().ifBlank { null },
                            )
                        }
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
                        text = stringResource(Res.string.create),
                        style = xsMedium.copy(
                            color = if (isValid) Base_white else Base_white.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

private const val MOBILE_LENGTH = 11

/** Accepts an empty string, digits, and at most one decimal separator with up to 4 integer digits. */
private fun isValidPriceInput(input: String): Boolean {
    if (input.isEmpty()) return true
    return input.matches(Regex("^\\d{0,4}(\\.\\d*)?$"))
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun CreateDeliveryOrderBottomSheetPreview() {
    CreateDeliveryOrderBottomSheet(
        onConfirm = { _, _, _, _ -> },
        onDismiss = {}
    )
}
