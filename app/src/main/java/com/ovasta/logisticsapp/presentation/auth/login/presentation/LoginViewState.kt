package com.ovasta.logisticsapp.presentation.auth.login.presentation

import com.ovasta.logisticsapp.base.UserType
import com.ovasta.logisticsapp.base.exception.ComposeUIException

data class LoginViewState(
    val phoneNumber: String = "",
    val password: String = "",
    val isPhoneValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isLoginButtonEnabled: Boolean = false,
    val selectedUserType: UserType = UserType.DELIVERY_AGENT,
    val error: ComposeUIException? = null,

    )