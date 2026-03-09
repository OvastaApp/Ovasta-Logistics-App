package com.ovasta.logisticsapp.base.components.sharedComposable


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray700
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smNormal

@Composable
fun ConfirmDialog(
    title: String,
    message: String? = null,
    onPrimaryClick: (() -> Unit),
    onDismiss: () -> Unit,
) {

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
                .background(
                    color = Base_white,
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
                )
                .padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                text = title,
                style = lgMedium.copy(color = Gray900)
            )

            Text(
                modifier = Modifier.padding(bottom = dimensionResource(com.intuit.sdp.R.dimen._24sdp)),
                text = message ?: "",
                style = smNormal.copy(color = Gray500),
                textAlign = TextAlign.Center
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = rememberSafeClick { onPrimaryClick() },
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Primary500
                ),
                contentPadding = PaddingValues(
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                )
            ) {

                Text(
                    text = stringResource(R.string.confirm),
                    style = mdMedium.copy(color = Base_white)
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
                onClick = { onDismiss() },
                shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                border = BorderStroke(
                    dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                    Gray300
                ),
                contentPadding = PaddingValues(
                    vertical = dimensionResource(com.intuit.sdp.R.dimen._10sdp),
                    horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                )
            ) {
                Text(
                    text = stringResource(R.string.dismiss),
                    style = smMedium.copy(color = Gray700)
                )
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun ConfirmCheckInDialogPreview() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        ConfirmDialog(
            title = "Confirm Action",
            message = "Are you sure you want to perform this action?",
            onPrimaryClick = { /* Handle confirm action */ },
            onDismiss = { /* Handle dismiss action */ }
        )

    }

}