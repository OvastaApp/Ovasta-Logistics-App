package com.ovasta.logisticsapp.presentation.auth.splash

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginScreen
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val settingsRepository: ISettingsRepository) : BaseViewModel() {

    fun navNextScreen() {
        viewModelScope.launch {
            delay(500)
            val loggedIn = settingsRepository.getUseData()?.deliveryId != null
            if (loggedIn) {
                emitScreenDirectionEvent(HomeScreen)

            } else {
                emitScreenDirectionEvent(LoginScreen)
            }
        }
    }

}