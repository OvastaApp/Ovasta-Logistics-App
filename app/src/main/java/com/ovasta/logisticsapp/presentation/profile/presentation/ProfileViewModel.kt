package com.ovasta.logisticsapp.presentation.profile.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val settingsRepository: ISettingsRepository
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
}
