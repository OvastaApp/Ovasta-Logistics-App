package com.ovasta.logisticsapp.presentation.profile.presentation.components

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.logout
import com.ovasta.logisticsapp.shared.resources.logout_message

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.base.components.sharedComposable.ConfirmDialog

@Composable
fun LogoutDialog(
    isVisible: Boolean? = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible == true) {
        ConfirmDialog(
            title = stringResource(Res.string.logout),
            message = stringResource(Res.string.logout_message),
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
