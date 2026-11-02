package com.ovasta.logisticsapp.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ovasta.logisticsapp.base.components.sharedComposable.LocalNavigator

sealed class ScreenDirection {
    data class Push(val screen: Any) : ScreenDirection()
    object Pop : ScreenDirection()
    data class Replace(val screen: Any) : ScreenDirection()
    /** Clears the whole back stack and shows [screen], e.g. Login after logout. */
    data class ReplaceAll(val screen: Any) : ScreenDirection()
}

@Composable
fun ScreenDirectionEventHandler(
    viewModel: BaseViewModel
) {
    // get the navigator from CompositionLocal
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.screenDirectionEvent.collect { direction ->
            when (direction) {
                is ScreenDirection.Push -> navigator.push(direction.screen)
                is ScreenDirection.Pop -> navigator.pop()
                is ScreenDirection.Replace -> navigator.replace(direction.screen)
                is ScreenDirection.ReplaceAll -> navigator.replaceAll(direction.screen)
                null -> Unit
            }
        }
    }
}