package com.ovasta.logisticsapp.presentation.auth.splash

import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.base.ScreenDirection
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.nav.Home
import com.ovasta.logisticsapp.presentation.nav.Login
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val settingsRepository: ISettingsRepository) : BaseViewModel() {

    init {
        navNextScreen()
    }

    private fun navNextScreen() {
        viewModelScope.launch {
            delay(500)

            val loggedIn = settingsRepository.getUseData()?.deliveryId != null

            if (loggedIn) {
                emitScreenDirectionEvent(ScreenDirection.Replace(Home))
            } else {
                emitScreenDirectionEvent(ScreenDirection.Replace(Login))
            }
        }
    }
}