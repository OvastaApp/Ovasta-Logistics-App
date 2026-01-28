package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.maxab.basemodule.listener.LogoutListener
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.ScreenDirectionEventHandler
import com.ovasta.logisticsapp.data.RemoteConstants
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun BaseScreen(
    viewModel: BaseViewModel,
    navController: NavController,
    content: @Composable () -> Unit
) {
    ScreenDirectionEventHandler(
        viewModel = viewModel,
        navController = navController
    )

    val logoutListener = LocalActivity.current as? LogoutListener

    /** Handling Base Exception **/
    val exceptionState by viewModel.composeUIExceptionEvent.collectAsState(null)
    exceptionState?.let {
        if (exceptionState?.code == RemoteConstants.UNAUTHORIZED_CODE) {
            logoutListener?.onUnauthorized()
        } else {

            BaseDialog(
                icon = painterResource(R.drawable.ic_error),
                title = stringResource(
                    exceptionState?.exceptionTitleResource
                        ?: R.string.an_error_occurred
                ),
                message = exceptionState?.getUIMessage(LocalContext.current)
                    ?: stringResource(R.string.generic_unknown_error),
                dismissOnClickOutside = true,
                primaryButtonText = stringResource(R.string.try_again),
                onPrimaryClick = {
                    exceptionState?.actions?.forEach { it.invoke() }
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

    ContextEventHandler(viewModel)

    content()
}