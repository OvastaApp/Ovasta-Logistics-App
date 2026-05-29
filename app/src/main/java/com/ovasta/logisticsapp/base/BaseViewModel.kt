package com.ovasta.logisticsapp.base

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovasta.logisticsapp.base.exception.ComposeUIException
import com.ovasta.logisticsapp.base.exception.toComposeUIException
import com.ovasta.logisticsapp.base.ext.ToastEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

typealias ContextEvent = (Context) -> Unit

open class BaseViewModel : ViewModel(), KoinComponent {
    val dispatcher: CoroutineDispatcher = Dispatchers.Main

    protected val error = SingleLiveEvent<Throwable>()
    protected val loading = MutableLiveData<Boolean>()

    private val _toastEvent = MutableSharedFlow<ToastEvent?>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val _contextEvent = MutableSharedFlow<ContextEvent?>()
    val contextEvent = _contextEvent.asSharedFlow()

    private val _screenDirectionEvent = MutableSharedFlow<ScreenDirection?>()
    val screenDirectionEvent = _screenDirectionEvent.asSharedFlow()

    private val _composeUIExceptionEvent = MutableStateFlow<ComposeUIException?>(null)
    val composeUIExceptionEvent = _composeUIExceptionEvent.asStateFlow()

    private val _composeUILoadingEvent = MutableStateFlow<Boolean>(false)
    val composeUILoadingEvent = _composeUILoadingEvent.asStateFlow()


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
    fun emitToastEvent(toastEvent: ToastEvent) {
        viewModelScope.launch { _toastEvent.emit(toastEvent) }
    }

}