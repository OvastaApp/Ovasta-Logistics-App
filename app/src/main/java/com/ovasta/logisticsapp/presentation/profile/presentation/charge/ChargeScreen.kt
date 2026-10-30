package com.ovasta.logisticsapp.presentation.profile.presentation.charge

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.presentation.profile.presentation.components.ProfileDetailsPlaceholder

@Composable
fun ChargeScreen() {
    val navigator = LocalNavigator.current

    ProfileDetailsPlaceholder(
        title = stringResource(R.string.charge),
        message = stringResource(R.string.charge_placeholder),
        onBack = { navigator.pop() }
    )
}
