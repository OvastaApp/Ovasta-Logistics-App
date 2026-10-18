package com.ovasta.logisticsapp.presentation.nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Holds the ViewModels of a logged-in session (e.g. HomeViewModel).
 * Kept as an Activity-scoped ViewModel so the session survives configuration changes,
 * and cleared on logout so the next account starts with fresh ViewModels.
 */
class SessionViewModelStoreOwner : ViewModel(), ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()

    fun clearSession() = viewModelStore.clear()

    override fun onCleared() = clearSession()
}
