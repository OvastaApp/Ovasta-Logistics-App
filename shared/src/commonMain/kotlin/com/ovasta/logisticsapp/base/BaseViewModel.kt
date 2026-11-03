package com.ovasta.logisticsapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.ext.ToastEvent
import com.ovasta.logisticsapp.base.services.LocationTrackingGuard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel : ViewModel(), KoinComponent {
    val dispatcher: CoroutineDispatcher = Dispatchers.Main

    private val _toastEvent = MutableSharedFlow<ToastEvent?>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val _platformAction = MutableSharedFlow<PlatformAction>()
    val platformAction = _platformAction.asSharedFlow()

    private val _screenDirectionEvent = MutableSharedFlow<ScreenDirection?>()
    val screenDirectionEvent = _screenDirectionEvent.asSharedFlow()

    private val _composeUIExceptionEvent = MutableStateFlow<ComposeUIException?>(null)
    val composeUIExceptionEvent = _composeUIExceptionEvent.asStateFlow()

    private val _composeUILoadingEvent = MutableStateFlow<Boolean>(false)
    val composeUILoadingEvent = _composeUILoadingEvent.asStateFlow()

    private val locationTrackingGuard: LocationTrackingGuard by inject()

    /**
     * Returns whether an order action may run now, i.e. the driver's location is tracked and
     * logged. When it may not, a dialog tells the driver why and how to fix it.
     */
    fun ensureLocationTracking(): Boolean = locationTrackingGuard.ensureLocationTracking()

    /** Asks the platform to do something only it can, e.g. reload the UI in a new language. */
    fun emitPlatformAction(action: PlatformAction) {
        viewModelScope.launch { _platformAction.emit(action) }
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
    fun emitToastEvent(toastEvent: ToastEvent) {
        viewModelScope.launch { _toastEvent.emit(toastEvent) }
    }

}
