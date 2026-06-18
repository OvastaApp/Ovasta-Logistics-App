package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium

/**
 * Dialog that collects a delivery-fees value (a non-negative double) and reports it on confirm.
 *
 * The input only accepts digits and a single decimal separator. The confirm button is enabled
 * only when the parsed value is within the inclusive range [0, [maxValue]].
 */
@Composable
fun DeliveryFeesDialog(
    title: String,
    message: String,
    maxValue: Double,
    confirmText: String,
    initialValue: Double? = null,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember {
        mutableStateOf(initialValue?.let { formatFees(it) } ?: "")
    }

    val parsed = text.toDoubleOrNull()
    val isValid = parsed != null && parsed in 0.0..maxValue

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            color = Base_white,
        ) {
            Column(modifier = Modifier.padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))) {

                Icon(
                    painter = painterResource(R.drawable.ic_delivery_truck),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(dimensionResource(com.intuit.sdp.R.dimen._24sdp))
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    style = lgMedium.copy(color = Gray900),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = message,
                    style = smNormal.copy(color = Gray500),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

                OutlinedTextField(
                    value = text,
                    textStyle = mdMedium,
                    onValueChange = { input ->
                        if (isValidFeesInput(input)) text = input
                    },
                    label = { Text(stringResource(R.string.delivery_fees_hint), style = xsMedium) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = dimensionResource(com.intuit.sdp.R.dimen._4sdp)),
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
                        onClick = { parsed?.let(onConfirm) },
                        shape = RoundedCornerShape(
                            dimensionResource(com.intuit.sdp.R.dimen._8sdp)
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Base_white,
                            disabledContainerColor = Primary.copy(alpha = 0.4f),
                            disabledContentColor = Base_white.copy(alpha = 0.7f)
                        ),
                    ) {
                        Text(
                            text = confirmText,
                            style = xsMedium.copy(
                                color = if (isValid) Base_white else Base_white.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }
    }
}

/** Accepts an empty string, digits, and at most one decimal separator with up to 4 integer digits. */
private fun isValidFeesInput(input: String): Boolean {
    if (input.isEmpty()) return true
    if (!input.matches(Regex("^\\d{0,4}(\\.\\d*)?$"))) return false
    return true
}

private fun formatFees(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeliveryFeesDialogPreview() {
    DeliveryFeesDialog(
        title = "Set delivery fees",
        message = "Enter the delivery fees for this order.",
        maxValue = 9999.99,
        confirmText = "Confirm",
        initialValue = 12.5,
        onConfirm = {},
        onDismiss = {}
    )

}