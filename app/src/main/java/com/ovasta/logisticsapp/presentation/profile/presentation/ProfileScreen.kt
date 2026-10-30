package com.ovasta.logisticsapp.presentation.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Gray600
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.base.smNormal
import com.ovasta.logisticsapp.presentation.nav.Charge
import com.ovasta.logisticsapp.presentation.nav.Wallet
import com.ovasta.logisticsapp.presentation.profile.presentation.components.LogoutDialog

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    BaseScreen(viewModel = viewModel) {
        ProfileContent(
            viewState = viewState,
            onChargeClick = { navigator.push(Charge) },
            onWalletClick = { navigator.push(Wallet) },
            onLogoutConfirmed = viewModel::logout
        )
    }
}

@Composable
fun ProfileContent(
    viewState: ProfileViewState,
    onChargeClick: () -> Unit,
    onWalletClick: () -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    var isLogoutDialogVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.profile),
                showBackButton = false,
                actions = {
                    IconButton(onClick = { isLogoutDialogVisible = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = stringResource(R.string.logout),
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Gray100)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._20sdp)))

            ProfileImagePlaceholder()

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._12sdp)))

            if (viewState.agentName.isNotBlank()) {
                Text(
                    text = viewState.agentName,
                    style = smNormal,
                    color = Gray600,
                    textAlign = TextAlign.Center
                )
            }

            if (viewState.phoneNumber.isNotBlank()) {
                // Keep the number left-to-right in Arabic so a leading "+" stays in front
                Text(
                    text = viewState.phoneNumber,
                    style = smNormal.copy(textDirection = TextDirection.Ltr),
                    color = Gray600
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._20sdp)))

            ProfileItemCard(
                title = stringResource(R.string.charge),
                onClick = onChargeClick
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._10sdp)))

            ProfileItemCard(
                title = stringResource(R.string.wallet),
                onClick = onWalletClick
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.intuit.sdp.R.dimen._20sdp)))
        }
    }

    LogoutDialog(
        isVisible = isLogoutDialogVisible,
        onConfirm = {
            isLogoutDialogVisible = false
            onLogoutConfirmed()
        },
        onDismiss = { isLogoutDialogVisible = false }
    )
}

// Placeholder until the agent's profile image is available
@Composable
private fun ProfileImagePlaceholder() {
    Box(
        modifier = Modifier
            .size(dimensionResource(com.intuit.sdp.R.dimen._70sdp))
            .clip(CircleShape)
            .background(Base_white),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = stringResource(R.string.profile_image),
            tint = Gray500,
            modifier = Modifier.size(dimensionResource(com.intuit.sdp.R.dimen._36sdp))
        )
    }
}

@Composable
private fun ProfileItemCard(title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Base_white),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(com.intuit.sdp.R.dimen._12sdp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = smMedium, color = Gray600)
            // The back arrow flipped, so it points forward in both LTR and RTL
            Icon(
                painter = painterResource(R.drawable.arrow_narrow_left),
                contentDescription = null,
                tint = Gray600,
                modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        viewState = ProfileViewState(
            agentName = "Ahmed Hassan",
            phoneNumber = "01012345678"
        ),
        onChargeClick = {},
        onWalletClick = {},
        onLogoutConfirmed = {}
    )
}
