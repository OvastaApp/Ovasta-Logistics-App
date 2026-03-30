package com.ovasta.logisticsapp.presentation.auth.login.presentation

import com.ovasta.logisticsapp.base.UserType
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.CenteredTextAppBar
import com.ovasta.logisticsapp.base.Gray100
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray800
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.components.sharedComposable.BaseScreen
import com.ovasta.logisticsapp.base.components.sharedComposable.ContextEventHandler
import com.ovasta.logisticsapp.base.mdMedium
import com.ovasta.logisticsapp.base.mdRegular
import com.ovasta.logisticsapp.base.smMedium
import com.ovasta.logisticsapp.presentation.auth.login.presentation.components.UserTypeOption

object LoginContentTestTag {

    private const val PREFIX = "LoginContentTestTag"
    const val ROOT = "$PREFIX.ROOT"
    const val BTN_LOGIN = "$PREFIX.BTN_LOHIN"
    const val TXT_INVALID_PHONE = "$PREFIX.TXT_INVALID_PHONE"
    const val TXT_INVALID_PASSWORD = "$PREFIX.TXT_INVALID_PASSWORD"
    const val TF_PHONE = "$PREFIX.TF_PHONE"
    const val TF_PASSWORD = "$PREFIX.TF_PASSWORD"
    const val IMG_MAXAB_LOGO = "$PREFIX.IMG_MAXAB_LOGO"
    const val IC_PASSWORD_VISIBILITY = "$PREFIX.IC_PASSWORD_VISIBILITY"
    const val BTN_DELIVERY_AGENT = "$PREFIX.BTN_DELIVERY_AGENT"
    const val BTN_PICKER = "$PREFIX.BTN_PICKER"
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    BaseScreen(viewModel = viewModel) {
        LoginContent(
            viewState = viewState,
            onAction = viewModel::onAction
        )
    }
    ContextEventHandler(viewModel)
}

@Composable
private fun LoginContent(
    viewState: LoginViewState,
    onAction: (LoginAction) -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenteredTextAppBar(
                title = stringResource(R.string.login),
                showBackButton = false
            ) {}
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding() // This makes the column adjust when keyboard appears
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Gray100)
                    .verticalScroll(rememberScrollState()) // Make content scrollable
                    .padding(horizontal = dimensionResource(com.intuit.sdp.R.dimen._16sdp))
            ) {
                // Phone Number Field
                Text(
                    modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._24sdp)),
                    text = stringResource(R.string.enter_your_phone_number),
                    style = smMedium
                )

                TextField(
                    modifier = Modifier
                        .testTag(LoginContentTestTag.TF_PHONE)
                        .fillMaxWidth()
                        .padding(top = dimensionResource(com.intuit.sdp.R.dimen._6sdp))
                        .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                        .border(
                            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                            if (viewState.isPhoneValid) Gray200 else Primary,
                            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                        ),
                    value = viewState.phoneNumber,
                    onValueChange = { input ->
                        if (input.length <= 11) {
                            onAction(LoginAction.PhoneNumberChanged(input))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Base_white,
                        unfocusedContainerColor = Base_white,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    placeholder = {
                        Text(
                            text = "01xxxxxxxxx",
                            style = mdRegular
                        )
                    },
                    maxLines = 1
                )

                if (!viewState.isPhoneValid) {
                    Text(
                        text = stringResource(R.string.invalid_phone_number),
                        color = Primary,
                        modifier = Modifier
                            .testTag(LoginContentTestTag.TXT_INVALID_PHONE)
                            .padding(top = dimensionResource(com.intuit.sdp.R.dimen._4sdp))
                    )
                }

                // Password Field
                Text(
                    modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
                    text = stringResource(R.string.password),
                    style = smMedium
                )

                TextField(
                    modifier = Modifier
                        .testTag(LoginContentTestTag.TF_PASSWORD)
                        .fillMaxWidth()
                        .padding(top = dimensionResource(com.intuit.sdp.R.dimen._6sdp))
                        .clip(RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)))
                        .border(
                            width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                            if (viewState.isPasswordValid) Gray200 else Primary,
                            shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp))
                        ),
                    value = viewState.password,
                    onValueChange = { input ->
                        if (input.length <= 20) {
                            onAction(LoginAction.PasswordChanged(input))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Base_white,
                        unfocusedContainerColor = Base_white,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_password),
                            style = mdRegular
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.testTag(LoginContentTestTag.IC_PASSWORD_VISIBILITY),
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(
                                modifier = Modifier.padding(end = dimensionResource(com.intuit.sdp.R.dimen._4sdp)),
                                painter = painterResource(
                                    if (passwordVisible)
                                        R.drawable.ic_visibility_on
                                    else
                                        R.drawable.ic_visibility_off
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Gray800
                            )
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboard?.hide()
                            if (viewState.isLoginButtonEnabled) {
                                onAction(LoginAction.Login)
                            }
                        }
                    ),
                    maxLines = 1
                )

                if (!viewState.isPasswordValid) {
                    Text(
                        text = stringResource(R.string.invalid_password),
                        color = Primary,
                        modifier = Modifier
                            .testTag(LoginContentTestTag.TXT_INVALID_PASSWORD)
                            .padding(top = dimensionResource(com.intuit.sdp.R.dimen._4sdp))
                    )
                }

                // User Type Selection
                Text(
                    modifier = Modifier.padding(top = dimensionResource(com.intuit.sdp.R.dimen._16sdp)),
                    text = stringResource(R.string.agent_type),
                    style = smMedium
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                            bottom = dimensionResource(com.intuit.sdp.R.dimen._16sdp)
                        ),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.intuit.sdp.R.dimen._12sdp))
                ) {

                    // Delivery Agent Option
                    UserTypeOption(
                        modifier = Modifier
                            .weight(1f)
                            .testTag(LoginContentTestTag.BTN_DELIVERY_AGENT),
                        icon = R.drawable.ic_delivery_agent,
                        label = stringResource(R.string.courier),
                        isSelected = viewState.selectedUserType == UserType.COURIER,
                        onClick = { onAction(LoginAction.UserTypeChanged(UserType.COURIER)) }
                    )

                    // Picker Option
                    UserTypeOption(
                        modifier = Modifier
                            .weight(1f)
                            .testTag(LoginContentTestTag.BTN_PICKER),
                        icon = R.drawable.ic_picker,
                        label = stringResource(R.string.picker),
                        isSelected = viewState.selectedUserType == UserType.WORKER,
                        onClick = { onAction(LoginAction.UserTypeChanged(UserType.WORKER)) }
                    )
                    // Admin Agent Option
                    UserTypeOption(
                        modifier = Modifier
                            .weight(1f)
                            .testTag(LoginContentTestTag.BTN_DELIVERY_AGENT),
                        icon = R.drawable.ic_admin,
                        label = stringResource(R.string.admin),
                        isSelected = viewState.selectedUserType == UserType.ADMIN,
                        onClick = { onAction(LoginAction.UserTypeChanged(UserType.ADMIN)) }
                    )
                }
            }

            // Login Button - stays at bottom, above keyboard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Base_white)
                    .border(
                        width = dimensionResource(com.intuit.sdp.R.dimen._1sdp),
                        color = Gray200
                    )
                    .padding(
                        end = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                        start = dimensionResource(com.intuit.sdp.R.dimen._16sdp),
                        top = dimensionResource(com.intuit.sdp.R.dimen._8sdp),
                        bottom = dimensionResource(com.intuit.sdp.R.dimen._8sdp)
                    ),
                contentAlignment = Alignment.Center,
            ) {

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(LoginContentTestTag.BTN_LOGIN),
                    onClick = { onAction(LoginAction.Login) },
                    shape = RoundedCornerShape(dimensionResource(com.intuit.sdp.R.dimen._8sdp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    enabled = viewState.isLoginButtonEnabled
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        style = mdMedium.copy(color = Base_white)
                    )
                }
            }

        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LoginContentPreview() {
    LoginContent(
        LoginViewState(
            phoneNumber = "123456789",
            password = "password123",
            isPhoneValid = true,
            isPasswordValid = true,
            selectedUserType = UserType.COURIER
        ),
        {}
    )
}