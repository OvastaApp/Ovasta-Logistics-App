package com.ovasta.logisticsapp.presentation.profile.presentation.wallet

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.presentation.profile.presentation.components.ProfileDetailsPlaceholder

@Composable
fun WalletScreen() {
    val navigator = LocalNavigator.current

    ProfileDetailsPlaceholder(
        title = stringResource(R.string.wallet),
        message = stringResource(R.string.wallet_placeholder),
        onBack = { navigator.pop() }
    )
}
