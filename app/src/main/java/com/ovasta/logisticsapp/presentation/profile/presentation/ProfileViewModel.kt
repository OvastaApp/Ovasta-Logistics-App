package com.ovasta.logisticsapp.presentation.profile.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.ScreenDirection
import com.ovasta.logisticsapp.base.exception.toComposeUIException
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

    private val _viewState = MutableStateFlow(ProfileViewState())
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
                    phoneNumber = user?.mobile.orEmpty()
                )
            }
        }
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
}
