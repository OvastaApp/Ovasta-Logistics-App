package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import com.ovasta.logisticsapp.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray900
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.lgMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.base.xsMedium

@Composable
fun BaseDialog(
    icon: Painter? = null,
    title: String? = null,
    message: String? = null,
    primaryButtonText: String? = null,
    secondaryButtonText: String? = null,
    onPrimaryClick: (() -> Unit)? = null,
    onSecondaryClick: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    dismissOnClickOutside: Boolean = true,
) {
    Dialog(
        onDismissRequest = {
            if (dismissOnClickOutside) onDismiss()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            color = Base_white,
        ) {
            Column(modifier = Modifier.padding(dimensionResource(com.intuit.sdp.R.dimen._16sdp))) {

                Icon(
                    painter = icon ?: painterResource(R.drawable.alert_circle_red),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(
                            dimensionResource(com.intuit.sdp.R.dimen._24sdp)
                        )
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._16sdp)))


                title?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it,
                        style = lgMedium.copy(color = Gray900),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                }

                message?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it,
                        style = smNormal.copy(color = Gray500),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    secondaryButtonText?.let {

                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = dimensionResource(com.intuit.sdp.R.dimen._4sdp)),
                            onClick = { onSecondaryClick?.invoke() },
                            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                            border = BorderStroke(
                                width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                                color = Primary
                            )
                        ) {
                            Text(
                                text = it,
                                style = xsMedium.copy(color = Primary)
                            )
                        }

                        /*TextButton(
                            onClick = {
                            onSecondaryClick?.invoke() ?: onDismiss()
                        }
                        ) {
                            Text(
                                text = it,
                                style = mdMedium.copy(color = Primary500)
                            )
                        }*/
                    }

                    primaryButtonText?.let {

                        Button(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                onPrimaryClick?.invoke()
                            },
                            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Primary,
                            ),
                        ) {
                            Text(
                                text = it,
                                style = xsMedium.copy(color = Base_white)
                            )
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun PreviewBaseDialog() {
    BaseDialog(
        title = stringResource(R.string.an_error_occurred),
        message = stringResource(R.string.generic_unknown_error),
        primaryButtonText = stringResource(R.string.try_again),
        secondaryButtonText = stringResource(R.string.back),
        onPrimaryClick = {},
        onSecondaryClick = {},
        onDismiss = {})
}