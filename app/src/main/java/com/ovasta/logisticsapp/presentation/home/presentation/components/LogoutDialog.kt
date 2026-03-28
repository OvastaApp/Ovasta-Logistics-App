package com.ovasta.logisticsapp.presentation.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.components.sharedComposable.ConfirmDialog

@Composable
fun LogoutDialog(
    isVisible: Boolean? = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible == true) {
        ConfirmDialog(
            title = stringResource(R.string.logout),
            message = stringResource(R.string.logout_message),
            onPrimaryClick = {
                onConfirm()
            },
            onDismiss = {
                onDismiss()
            }
        )
    }
}
@Preview
@Composable
fun PreviewLogoutDialog() {
    LogoutDialog(
        isVisible = true,
        onConfirm = {},
        onDismiss = {}
    )
}
