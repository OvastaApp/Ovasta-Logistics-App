package com.ovasta.logisticsapp.presentation.auth.login.presentation

import com.ovasta.logisticsapp.base.UserType

sealed interface LoginAction {
    data class PhoneNumberChanged(val phoneNumber: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    data class UserTypeChanged(val type: UserType) : LoginAction
    object Login : LoginAction


}