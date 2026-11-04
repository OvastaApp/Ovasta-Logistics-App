package com.ovasta.logisticsapp.presentation.auth.splash

sealed interface SplashAction {
    object NextScreen : SplashAction
}