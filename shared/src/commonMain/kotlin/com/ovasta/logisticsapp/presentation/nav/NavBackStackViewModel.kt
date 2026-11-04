package com.ovasta.logisticsapp.presentation.nav

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

/**
 * Holds the navigation back stack in an Activity-scoped ViewModel so it survives the Activity
 * being recreated (e.g. after changing the language) instead of restarting from Splash.
 */
class NavBackStackViewModel : ViewModel() {
    val backStack = mutableStateListOf<Any>(Splash)
}
