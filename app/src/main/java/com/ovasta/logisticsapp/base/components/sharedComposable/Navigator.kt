package com.ovasta.logisticsapp.base.components.sharedComposable

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("Navigator not provided")
}
class Navigator(
    private val backStack: SnapshotStateList<Any>
) {
    fun push(screen: Any) {
        backStack.add(screen)
    }

    fun replace(screen: Any) {
        backStack.removeLastOrNull()
        backStack.add(screen)
    }

    fun pop() {
        backStack.removeLastOrNull()
    }

    fun replaceAll(screen: Any) {
        backStack.clear()
        backStack.add(screen)
    }

    // Removes every screen above the last [screen] in the back stack
    fun popUpTo(screen: Any) {
        val index = backStack.lastIndexOf(screen)
        if (index == -1) return
        while (backStack.lastIndex > index) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}