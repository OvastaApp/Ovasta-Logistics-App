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
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.StatusDelivered
import com.ovasta.logisticsapp.base.StatusPending
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct

/**
 * Bottom sheet that lets the user mark a product as picked, either fully (found == required)
 * or with a missing amount (found < required).
 *
 * The found-quantity field is pre-filled with the product's previously picked amount, or the
 * required quantity when picking for the first time. "All found" sets it to the required amount.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickProductBottomSheet(
    product: FirebaseProduct,
    onConfirm: (foundQuantity: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val requiredQuantity = product.quantity ?: 0
    val initialFound = product.pickedQuantity ?: requiredQuantity

    var foundText by remember { mutableStateOf(initialFound.toString()) }
    val parsedFound = foundText.toIntOrNull()

    val isFoundValid = parsedFound != null && parsedFound in 0..requiredQuantity
    val statusColor = when {
        parsedFound == requiredQuantity -> StatusDelivered
        else -> StatusPending
    }

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
                text = stringResource(R.string.pick_product),
                style = lgMedium.copy(color = Gray900),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._4sdp)))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = product.name.orEmpty(),
                style = smNormal.copy(color = Gray500),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.required_qty, requiredQuantity),
                style = mdMedium.copy(color = Gray900),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

            OutlinedTextField(
                value = foundText,
                textStyle = mdMedium,
                onValueChange = { input -> if (isValidIntInput(input)) foundText = input },
                label = { Text(stringResource(R.string.found_quantity), style = xsMedium) },
                singleLine = true,
                isError = foundText.isNotEmpty() && !isFoundValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { foundText = requiredQuantity.toString() },
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
            ) {
                Text(
                    text = stringResource(R.string.pick_all_found, requiredQuantity),
                    style = xsMedium.copy(color = StatusDelivered)
                )
            }

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
                    enabled = isFoundValid,
                    onClick = {
                        val found = parsedFound ?: return@Button
                        onConfirm(found)
                    },
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = statusColor,
                        contentColor = Base_white,
                        disabledContainerColor = statusColor.copy(alpha = 0.4f),
                        disabledContentColor = Base_white.copy(alpha = 0.7f)
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.confirm_pick),
                        style = xsMedium.copy(
                            color = if (isFoundValid) Base_white else Base_white.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

/** Accepts an empty string or up to 6 digits (no decimals, no separators). */
private fun isValidIntInput(input: String): Boolean {
    if (input.isEmpty()) return true
    return input.matches(Regex("^\\d{0,6}$"))
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PickProductBottomSheetPreview() {
    PickProductBottomSheet(
        product = FirebaseProduct(name = "Pepsi 1L", itemPrice = 20, quantity = 2),
        onConfirm = {},
        onDismiss = {}
    )
}
