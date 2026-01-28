package com.ovasta.logisticsapp.base.exception

import android.content.Context
import androidx.annotation.StringRes
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ovasta.logisticsapp.R
import java.io.IOException

data class ComposeUIException(
    @StringRes val exceptionTitleResource: Int? = null,
    @SerializedName("msg", alternate = ["message"]) var errorMessage: String?,
    @StringRes val exceptionMessageResource: Int? = null,
    @Expose var code: Int?,
    @SerializedName("data") val data: Any? = null,
    val actions: List<(() -> Unit)> = listOf(),
) : IOException() {

    fun getUIMessage(context: Context): String {
        return if (exceptionMessageResource != null) {
            context.getString(exceptionMessageResource)
        } else {
            errorMessage ?: context.getString(R.string.generic_unknown_error)
        }
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
                exceptionMessageResource = R.string.generic_unknown_error
            )
        }

    }
}