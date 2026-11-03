package com.ovasta.logisticsapp.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/** Things a ViewModel asks the platform to do; see [PlatformActionHandler]. */
sealed interface PlatformAction {
    /** The app language changed: reload every screen in it and its layout direction. */
    data object LanguageChanged : PlatformAction
}

/** Performs the ViewModel's [PlatformAction]s. */
@Composable
fun PlatformActionHandler(viewModel: BaseViewModel) {
    val perform = rememberPlatformActionPerformer()
    LaunchedEffect(viewModel.platformAction) {
        viewModel.platformAction.collect { perform(it) }
    }
}

@Composable
internal expect fun rememberPlatformActionPerformer(): (PlatformAction) -> Unit
