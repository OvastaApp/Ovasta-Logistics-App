package com.ovasta.logisticsapp.presentation.auth.login.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.ScreenDirection
import com.ovasta.logisticsapp.base.UserType
import com.ovasta.logisticsapp.base.exception.APIException
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.ext.ToastHelper
import com.ovasta.logisticsapp.data.RemoteConstants
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.auth.login.data.ILoginRepository
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreen
import com.ovasta.logisticsapp.presentation.nav.Home
import kotlinx.coroutines.CoroutineExceptionHandler
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
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
        val exception = throwable.toComposeUIException().also {
            if (it.code == RemoteConstants.UNAUTHORIZED_CODE) {
                it.code = 0
            }
        }
        emitComposeUIExceptionEvent(exception)
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
            is LoginAction.ResetState -> resetState()
        }
    }

    private fun resetState() {
        _viewState.value = LoginViewState()
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
            updateViewStateWithFail(throwable)
        }

        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            setComposeUILoading(true)
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                null
            }
            runCatching {
                loginRepository.login(phone, password, userType.typeId, fcmToken)
            }.onSuccess { response ->
                val user = response.data
                user.token = response.token
                settingsRepository.saveUserData(user)
                settingsRepository.saveFcmToken(fcmToken ?: "")
                setComposeUILoading(false)
                emitScreenDirectionEvent(ScreenDirection.Replace(Home))
            }.onFailure {
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