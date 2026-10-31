package com.ovasta.logisticsapp.presentation.profile.presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.ext.isValidDecimalInput
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.xsMedium

/**
 * Bottom sheet for a new top-up request: the agent enters the amount and attaches a photo of the
 * transfer, taken with the camera or picked from the gallery. The form state is hoisted so it
 * survives the trip to the camera / gallery app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTopUpBottomSheet(
    amountText: String,
    onAmountChange: (String) -> Unit,
    proofImage: Uri?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onRemoveImage: () -> Unit,
    onSubmit: (amount: Double, proof: Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val parsedAmount = amountText.toDoubleOrNull()?.takeIf { it > 0.0 }
    val isValid = parsedAmount != null && proofImage != null

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
                text = stringResource(R.string.new_top_up),
                style = lgMedium.copy(color = Gray900),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))

            OutlinedTextField(
                value = amountText,
                textStyle = mdMedium,
                onValueChange = { input ->
                    if (isValidDecimalInput(input, maxIntegerDigits = 7, maxDecimals = 2)) onAmountChange(input)
                },
                label = { Text(stringResource(R.string.top_up_amount), style = xsMedium) },
                // The currency alone, without an amount
                suffix = { Text(stringResource(R.string.price_currency, "").trim(), style = xsMedium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))

            Text(text = stringResource(R.string.top_up_proof), style = smMedium.copy(color = Gray800))

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))

            if (proofImage != null) {
                ProofImagePreview(image = proofImage, onRemove = onRemoveImage)
            } else {
                // Retaking means removing the current photo first: a new capture deletes the old file
                ProofImageSourceButtons(onCameraClick = onCameraClick, onGalleryClick = onGalleryClick)
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
                        val amount = parsedAmount ?: return@Button
                        val proof = proofImage ?: return@Button
                        onSubmit(amount, proof)
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
                        text = stringResource(R.string.send_request),
                        style = xsMedium.copy(
                            color = if (isValid) Base_white else Base_white.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProofImageSourceButtons(onCameraClick: () -> Unit, onGalleryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
            .background(Gray100)
            .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.top_up_proof_hint),
            style = xsMedium.copy(color = Gray500),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
        ) {
            ImageSourceButton(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_camera,
                label = stringResource(R.string.camera),
                onClick = onCameraClick
            )
            ImageSourceButton(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_image,
                label = stringResource(R.string.gallery),
                onClick = onGalleryClick
            )
        }
    }
}

@Composable
private fun ImageSourceButton(
    icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Base_white)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._18sdp))
        )
        Spacer(modifier = Modifier.width(dimensionResource(com.intuit.sdp.R.dimen._6sdp)))
        Text(text = label, style = smMedium.copy(color = Primary), maxLines = 1)
    }
}

@Composable
private fun ProofImagePreview(image: Uri, onRemove: () -> Unit) {
    val shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(com.intuit.sdp.R.dimen._160sdp))
            .clip(shape)
            .background(Gray100)
            .border(
                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                color = Gray200,
                shape = shape
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(image),
            contentDescription = stringResource(R.string.top_up_proof_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimensionResource(com.intuit.sdp.R.dimen._6sdp))
                .size(dimensionResource(com.intuit.sdp.R.dimen._28sdp))
                .clip(CircleShape)
                .background(Base_white.copy(alpha = 0.9f))
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.remove_image),
                tint = Gray800,
                modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._14sdp))
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun NewTopUpBottomSheetPreview() {
    NewTopUpBottomSheet(
        amountText = "100.50",
        onAmountChange = {},
        proofImage = null,
        onCameraClick = {},
        onGalleryClick = {},
        onRemoveImage = {},
        onSubmit = { _, _ -> },
        onDismiss = {}
    )
}
