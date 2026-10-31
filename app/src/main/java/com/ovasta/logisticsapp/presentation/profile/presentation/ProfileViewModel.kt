package com.ovasta.logisticsapp.presentation.profile.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.ScreenDirection
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.local.AppLanguage
import com.ovasta.logisticsapp.base.local.LocaleHelper
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.home.data.IHomeRepository
import com.ovasta.logisticsapp.presentation.nav.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val context: Context,
    private val settingsRepository: ISettingsRepository,
    private val homeRepository: IHomeRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(
        ProfileViewState(language = AppLanguage.fromCode(LocaleHelper.getLanguage(context)))
    )
    val viewState = _viewState.asStateFlow()

    init {
        loadAgentInfo()
        loadOutsourceStatus()
    }

    private fun loadAgentInfo() {
        viewModelScope.launch {
            val user = settingsRepository.getUseData()
            _viewState.update {
                it.copy(
                    agentName = user?.name.orEmpty(),
                    phoneNumber = user?.mobile.orEmpty()
                )
            }
        }
    }

    // Whether the agent is outsource comes from `home`, not the login response. A failure is
    // only logged: Home already shows the error for the same call.
    private fun loadOutsourceStatus() {
        viewModelScope.launch {
            kotlin.runCatching {
                homeRepository.getHome()
            }.onSuccess { response ->
                _viewState.update { it.copy(isOutsource = response.data?.courier?.isOutsource == true) }
            }.onFailure {
                Log.e("ProfileViewModel", "Failed to load home data", it)
            }
        }
    }

    fun changeLanguage(language: AppLanguage) {
        if (language == _viewState.value.language) return
        LocaleHelper.setLanguage(context, language.code)
        _viewState.update { it.copy(language = language) }
        // Recreating the Activity reloads every screen in the new language and layout direction
        emitContextEvent { it.findActivity()?.recreate() }
    }

    fun logout() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                settingsRepository.logout()
            }.onSuccess {
                setComposeUILoading(false)
                homeRepository.stopLocationTracking(context)
                settingsRepository.clearUserData()
                // Replacing the whole back stack removes Home, which ends the session: its
                // ViewModels are cleared, stopping the Firebase listeners and the order alarm
                emitScreenDirectionEvent(ScreenDirection.ReplaceAll(Login))
            }.onFailure {
                setComposeUILoading(false)
                emitComposeUIExceptionEvent(it.toComposeUIException())
            }
        }
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
