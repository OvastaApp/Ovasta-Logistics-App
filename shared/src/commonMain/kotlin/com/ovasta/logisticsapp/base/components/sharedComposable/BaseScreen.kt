package com.ovasta.logisticsapp.base.components.sharedComposable

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.an_error_occurred
import com.ovasta.logisticsapp.shared.resources.dismiss
import com.ovasta.logisticsapp.shared.resources.ic_error

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.PlatformActionHandler
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.ScreenDirectionEventHandler
import com.ovasta.logisticsapp.base.ext.ToastEventHandler
import com.ovasta.logisticsapp.data.RemoteConstants
import com.ovasta.logisticsapp.listener.LocalLogoutListener
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    content: @Composable () -> Unit
) {
    ScreenDirectionEventHandler(
        viewModel = viewModel
    )
    val logoutListener = LocalLogoutListener.current
    ToastEventHandler(viewModel)

    /** Handling Base Exception **/
    val exceptionState by viewModel.composeUIExceptionEvent.collectAsState(null)
    exceptionState?.let { exception ->
        if (exception.code == RemoteConstants.UNAUTHORIZED_CODE) {
            logoutListener?.onUnauthorized()
        } else {
            BaseDialog(
                icon = painterResource(Res.drawable.ic_error),
                title = stringResource(exception.exceptionTitleResource ?: Res.string.an_error_occurred),
                message = exception.uiMessage(),
                dismissOnClickOutside = true,
                primaryButtonText = stringResource(Res.string.dismiss),
                onPrimaryClick = {
                    exception.actions.forEach { it.invoke() }
                    viewModel.emitComposeUIExceptionEvent(null)
                },
                onDismiss = { viewModel.emitComposeUIExceptionEvent(null) }
            )
        }
    }

    /** Handling Base Loading **/
    val isLoading by viewModel.composeUILoadingEvent.collectAsState(false)
    if (isLoading) {
        Dialog(
            onDismissRequest = {}
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Primary
                )
            }
        }
    }

    PlatformActionHandler(viewModel)
    content()
}
