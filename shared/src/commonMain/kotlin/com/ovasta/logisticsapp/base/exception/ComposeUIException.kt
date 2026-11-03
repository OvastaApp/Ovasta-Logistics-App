package com.ovasta.logisticsapp.base.exception

import androidx.compose.runtime.Composable
import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.generic_unknown_error
import kotlinx.io.IOException
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class ComposeUIException(
    val exceptionTitleResource: StringResource? = null,
    var errorMessage: String?,
    val exceptionMessageResource: StringResource? = null,
    var code: Int?,
    val data: Any? = null,
    val actions: List<(() -> Unit)> = listOf(),
) : IOException() {

    @Composable
    fun uiMessage(): String = when {
        exceptionMessageResource != null -> stringResource(exceptionMessageResource)
        else -> errorMessage ?: stringResource(Res.string.generic_unknown_error)
    }

}


fun Throwable.toComposeUIException(vararg actions: () -> Unit): ComposeUIException {
    return when(this) {

        is APIException -> {
            ComposeUIException(
                code = this.code,
                errorMessage = this.errorMessage,
                data = this.data,
                actions = actions.toList(),

                )
        }

        is NetworkException -> {
            ComposeUIException(
                code = 0,
                errorMessage = this.message,
                actions = actions.toList()
            )
        }

        is ComposeUIException -> this

        else -> {
            ComposeUIException(
                code = 0,
                errorMessage = null,
                actions = actions.toList(),
                exceptionMessageResource = Res.string.generic_unknown_error
            )
        }

    }
}
