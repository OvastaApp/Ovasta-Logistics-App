package com.ovasta.logisticsapp.presentation.profile.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.PlatformAction
import com.ovasta.logisticsapp.base.ScreenDirection
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.services.LocationTrackingSwitch
import com.ovasta.logisticsapp.base.local.AppLanguage
import com.ovasta.logisticsapp.base.local.AppLanguageStore
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.nav.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val languageStore: AppLanguageStore,
    private val settingsRepository: ISettingsRepository,
    private val locationTrackingSwitch: LocationTrackingSwitch
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(
        ProfileViewState(language = AppLanguage.fromCode(languageStore.language()))
    )
    val viewState = _viewState.asStateFlow()

    init {
        loadAgentInfo()
    }

    private fun loadAgentInfo() {
        viewModelScope.launch {
            val user = settingsRepository.getUseData()
            _viewState.update {
                it.copy(
                    agentName = user?.name.orEmpty(),
                    phoneNumber = user?.mobile.orEmpty(),
                    isOutsource = user?.isOutsource == true
                )
            }
        }
    }

    fun changeLanguage(language: AppLanguage) {
        if (language == _viewState.value.language) return
        languageStore.setLanguage(language.code)
        _viewState.update { it.copy(language = language) }
        // Recreating the Activity reloads every screen in the new language and layout direction
        emitPlatformAction(PlatformAction.LanguageChanged)
    }

    fun logout() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                settingsRepository.logout()
            }.onSuccess {
                setComposeUILoading(false)
                locationTrackingSwitch.stopLocationTracking()
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
}
