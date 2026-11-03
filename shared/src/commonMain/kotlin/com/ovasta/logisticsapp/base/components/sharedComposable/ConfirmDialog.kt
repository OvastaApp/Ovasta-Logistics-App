package com.ovasta.logisticsapp.base.components.sharedComposable

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.confirm
import com.ovasta.logisticsapp.shared.resources.dismiss


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray300
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray700
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.Primary500
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.ext.sdp

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
                .clip(RoundedCornerShape(12.sdp))
                .background(
                    color = Base_white,
                    shape = RoundedCornerShape(12.sdp)
                )
                .padding(16.sdp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.padding(bottom = 8.sdp),
                text = title,
                style = lgMedium.copy(color = Gray900)
            )

            Text(
                modifier = Modifier.padding(bottom = 24.sdp),
                text = message ?: "",
                style = smNormal.copy(color = Gray500),
                textAlign = TextAlign.Center
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = rememberSafeClick { onPrimaryClick() },
                shape = RoundedCornerShape(8.sdp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                // Material 1's default, which this button had before
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                contentPadding = PaddingValues(
                    vertical = 10.sdp,
                    horizontal = 16.sdp
                )
            ) {
                Text(
                    text = stringResource(Res.string.confirm),
                    style = mdMedium.copy(color = Base_white)
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.sdp),
                onClick = { onDismiss() },
                shape = RoundedCornerShape(8.sdp),
                border = BorderStroke(
                    1.sdp,
                    Gray300
                ),
                contentPadding = PaddingValues(
                    vertical = 10.sdp,
                    horizontal = 16.sdp
                )
            ) {
                Text(
                    text = stringResource(Res.string.dismiss),
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