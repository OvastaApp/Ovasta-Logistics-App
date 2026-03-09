package com.ovasta.logisticsapp.presentation.auth.login.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.UserType
import com.ovasta.logisticsapp.base.exception.APIException
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.ext.ToastHelper
import com.ovasta.logisticsapp.data.RemoteConstants
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.auth.login.data.ILoginRepository
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreen
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: ILoginRepository,
    private val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState = _viewState.asStateFlow()

    fun updateViewState(update: (LoginViewState) -> LoginViewState) {
        _viewState.update(update)
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }


    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.PhoneNumberChanged -> onPhoneNumberChanged(action.phoneNumber)
            is LoginAction.PasswordChanged -> onPasswordChanged(action.password)
            is LoginAction.UserTypeChanged -> onUserTypeChanged(action.type)
            is LoginAction.Login -> with(viewState.value) {
                login(
                    this.phoneNumber,
                    this.password,
                    this.selectedUserType
                )
            }
        }
    }

    private fun onUserTypeChanged(type: UserType) {
        updateViewState { state ->
            state.copy(
                selectedUserType = type
            )
        }
    }

    private fun onPhoneNumberChanged(phoneNumber: String) = viewModelScope.launch {
        val isValid = isValidMobile(phoneNumber)
        updateViewState { state ->
            state.copy(
                phoneNumber = phoneNumber, isPhoneValid = isValid
            )
        }
        checkBtnEnabled()
    }

    private fun onPasswordChanged(password: String) = viewModelScope.launch {
        val isValid = isValidPassword(password)
        updateViewState { state ->
            state.copy(
                password = password, isPasswordValid = isValid
            )
        }
        checkBtnEnabled()
    }

    fun checkBtnEnabled() = viewModelScope.launch {
        val isPhoneValid = isValidMobile(viewState.value.phoneNumber)
        val isPasswordValid = isValidPassword(viewState.value.password)

        updateViewState { state ->
            state.copy(isLoginButtonEnabled = isPhoneValid && isPasswordValid)
        }
    }


    private fun login(phone: String, password: String, userType: UserType) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            setComposeUILoading(false)
            if (throwable is APIException) {
                if (throwable.code == RemoteConstants.UNAUTHORIZED_CODE) {
                    emitContextEvent { context ->
                        ToastHelper.showLongToaster(
                            context, throwable.errorMessage
                        )
                    }
                } else {
                    error.value = throwable
                }
            } else {
                error.value = throwable
            }
        }

        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            setComposeUILoading(true)
            delay(10000)
            runCatching {
                loginRepository.login(phone, password, userType.typeId)
            }.onSuccess { user ->
                user.userType = userType
                settingsRepository.saveUserData(user)
                setComposeUILoading(false)
//                loginRepository.authenticateWithFirebase(user.firebaseToken, onSuccess = {
//                    setComposeUILoading(false)
//                    emitScreenDirectionEvent(HomeScreen)
//                }, onFailure = {
//                    setComposeUILoading(false)
//                })
            }.onFailure {
                if (it is APIException) {
                    updateViewState { state -> state.copy(isPhoneValid = false) }
                }
                updateViewStateWithFail(it)
            }
        }
    }

    private fun isValidMobile(mobile: String): Boolean {
        return when {
            mobile.isEmpty() -> false
            mobile.length != 11 -> false
            else -> true
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return when {
            password.isEmpty() -> false
            password.length < 4 -> false
            else -> true
        }
    }
}