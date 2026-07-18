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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
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
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium
import com.ovasta.logisticsapp.presentation.home.data.model.FirebaseProduct
import com.ovasta.logisticsapp.presentation.orderDetails.data.model.ProductSource

/**
 * Bottom sheet that lets the user edit a product's unit price, quantity and/or source.
 *
 * Fields are pre-filled with the product's current values. The source dropdown loads its options
 * lazily via [onLoadSources] the first time it is expanded. Save is enabled only when the inputs
 * are valid (non-empty, within bounds) and at least one value has actually changed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductBottomSheet(
    product: FirebaseProduct,
    availableSources: List<ProductSource>,
    isSourcesLoading: Boolean,
    onLoadSources: () -> Unit,
    onSave: (newPrice: Int, newQuantity: Int, newSource: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val initialPrice = product.itemPrice ?: 0
    val initialQuantity = product.quantity ?: 0
    val initialSource = product.source.orEmpty()

    var priceText by remember { mutableStateOf(initialPrice.toString()) }
    var quantityText by remember { mutableStateOf(initialQuantity.toString()) }
    var selectedSource by remember { mutableStateOf(initialSource) }

    val parsedPrice = priceText.toIntOrNull()
    val parsedQuantity = quantityText.toIntOrNull()

    val isPriceValid = parsedPrice != null && parsedPrice in 0..MAX_PRICE
    val isQuantityValid = parsedQuantity != null && parsedQuantity in 1..MAX_QUANTITY
    val hasChanged = parsedPrice != initialPrice ||
        parsedQuantity != initialQuantity ||
        selectedSource != initialSource
    val isValid = isPriceValid && isQuantityValid && hasChanged

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
                text = stringResource(R.string.edit_product),
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

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))

            OutlinedTextField(
                value = priceText,
                textStyle = mdMedium,
                onValueChange = { input -> if (isValidIntInput(input)) priceText = input },
                label = { Text(stringResource(R.string.price), style = xsMedium) },
                singleLine = true,
                isError = priceText.isNotEmpty() && !isPriceValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            OutlinedTextField(
                value = quantityText,
                textStyle = mdMedium,
                onValueChange = { input -> if (isValidIntInput(input)) quantityText = input },
                label = { Text(stringResource(R.string.quantity), style = xsMedium) },
                singleLine = true,
                isError = quantityText.isNotEmpty() && !isQuantityValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            // Source dropdown — options are fetched from the API the first time it is opened.
            var sourceExpanded by remember { mutableStateOf(false) }
            var sourcesRequested by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = sourceExpanded,
                onExpandedChange = { expanded ->
                    sourceExpanded = expanded
                    if (expanded && !sourcesRequested) {
                        sourcesRequested = true
                        onLoadSources()
                    }
                }
            ) {
                OutlinedTextField(
                    value = selectedSource,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = mdMedium,
                    label = { Text(stringResource(R.string.source), style = xsMedium) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = sourceExpanded,
                    onDismissRequest = { sourceExpanded = false }
                ) {
                    when {
                        isSourcesLoading -> DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._14sdp)),
                                        strokeWidth = dimensionResource(com.intuit.sdp.R.dimen._2sdp),
                                        color = Primary
                                    )
                                    Text(
                                        text = stringResource(R.string.loading_sources),
                                        style = smNormal.copy(color = Gray500),
                                        modifier = Modifier.padding(
                                            start = dimensionResource(com.intuit.sdp.R.dimen._8sdp)
                                        )
                                    )
                                }
                            },
                            onClick = {}
                        )

                        availableSources.isEmpty() -> DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.no_sources_available),
                                    style = smNormal.copy(color = Gray500)
                                )
                            },
                            onClick = { sourceExpanded = false }
                        )

                        else -> availableSources.forEach { source ->
                            DropdownMenuItem(
                                text = { Text(text = source.name.orEmpty(), style = mdMedium) },
                                onClick = {
                                    selectedSource = source.name.orEmpty()
                                    sourceExpanded = false
                                }
                            )
                        }
                    }
                }
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
                    enabled = isValid,
                    onClick = {
                        val price = parsedPrice ?: return@Button
                        val quantity = parsedQuantity ?: return@Button
                        onSave(price, quantity, selectedSource.takeIf { it.isNotBlank() })
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
                        text = stringResource(R.string.save_changes),
                        style = xsMedium.copy(
                            color = if (isValid) Base_white else Base_white.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

private const val MAX_PRICE = 999_999
private const val MAX_QUANTITY = 9_999

/** Accepts an empty string or up to 6 digits (no decimals, no separators). */
private fun isValidIntInput(input: String): Boolean {
    if (input.isEmpty()) return true
    return input.matches(Regex("^\\d{0,6}$"))
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun EditProductBottomSheetPreview() {
    EditProductBottomSheet(
        product = FirebaseProduct(name = "Product 1", itemPrice = 100, quantity = 2),
        availableSources = listOf(ProductSource(id = 1, name = "زيت وسمنه")),
        isSourcesLoading = false,
        onLoadSources = {},
        onSave = { _, _, _ -> },
        onDismiss = {}
    )
}
