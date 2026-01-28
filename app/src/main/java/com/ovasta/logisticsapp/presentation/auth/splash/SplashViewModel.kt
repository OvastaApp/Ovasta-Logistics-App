package com.ovasta.logisticsapp.presentation.auth.splash

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.BaseViewModel
import com.ovasta.logisticsapp.presentation.auth.login.data.ILoginRepository
import com.ovasta.logisticsapp.presentation.auth.login.presentation.LoginScreen
import com.ovasta.logisticsapp.presentation.home.presentation.HomeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val loginRepository: ILoginRepository
) : BaseViewModel() {

    init {
        setArabicLocale()
    }

    fun setArabicLocale() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ar"))
    }

    fun navNextScreen() {
        viewModelScope.launch {
            delay(1000)
            emitScreenDirectionEvent(HomeScreen)

//            val loggedIn = loginRepository.isUserLoggedIn().first()
//            if (loggedIn) {
//                emitScreenDirectionEvent(HomeScreen)
//            } else {
//                emitScreenDirectionEvent(LoginScreen)
//            }
        }
    }
}