package com.ovasta.logisticsapp.base

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.data.setting.data.ISettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

typealias NavControllerEvent = (NavController) -> Unit
typealias ContextEvent = (Context) -> Unit

open class BaseViewModel : ViewModel(), KoinComponent {
    val dispatcher: CoroutineDispatcher = Dispatchers.Main

    protected val error = SingleLiveEvent<Throwable>()
    protected val loading = MutableLiveData<Boolean>()

    private val _navControllerEvent = MutableSharedFlow<NavControllerEvent?>()
    val navControllerEvent = _navControllerEvent.asSharedFlow()

    private val _contextEvent = MutableSharedFlow<ContextEvent?>()
    val contextEvent = _contextEvent.asSharedFlow()

    private val _screenDirectionEvent = MutableSharedFlow<ScreenDirection?>()
    val screenDirectionEvent = _screenDirectionEvent.asSharedFlow()

    private val _composeUIExceptionEvent = MutableStateFlow<ComposeUIException?>(null)
    val composeUIExceptionEvent = _composeUIExceptionEvent.asStateFlow()

    private val _composeUILoadingEvent = MutableStateFlow<Boolean>(false)
    val composeUILoadingEvent = _composeUILoadingEvent.asStateFlow()

    private val settingsRepository: ISettingsRepository by inject()

    fun getErrorLiveData(): LiveData<Throwable> = error

    fun getLoading(): LiveData<Boolean> = loading

    fun loadingOn() {
        loading.postValue(true)
    }

    fun loadingOff() {
        loading.postValue(false)
    }

    fun emitNavigationEvent(event: NavControllerEvent) {
        viewModelScope.launch { _navControllerEvent.emit(event) }
    }

    fun emitContextEvent(event: ContextEvent) {
        viewModelScope.launch { _contextEvent.emit(event) }
    }

    fun emitScreenDirectionEvent(direction: ScreenDirection) {
        viewModelScope.launch { _screenDirectionEvent.emit(direction) }
    }

    fun emitComposeUIExceptionEvent(exception: ComposeUIException?) {
        viewModelScope.launch { _composeUIExceptionEvent.emit(exception) }
    }

    fun setComposeUILoading(isLoading: Boolean) {
        viewModelScope.launch { _composeUILoadingEvent.emit(isLoading) }
    }
    protected fun emitComposeUIExceptionEvent(newAction: () -> Unit, throwable: Throwable) {
        val current = _composeUIExceptionEvent.value
        val newEvent = throwable.toComposeUIException(newAction)

        if (current == null) {
            _composeUIExceptionEvent.value = newEvent
        } else {
            _composeUIExceptionEvent.value = current.copy(
                actions = current.actions.toMutableList().apply {
                    addAll(newEvent.actions)
                }
            )
        }
    }

}