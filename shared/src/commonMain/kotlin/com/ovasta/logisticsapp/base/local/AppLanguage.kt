package com.ovasta.logisticsapp.base.local

import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_EN_ISO
import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.language_arabic
import com.ovasta.logisticsapp.shared.resources.language_english
import org.jetbrains.compose.resources.StringResource

/** Languages the agent can pick in Profile. */
enum class AppLanguage(val code: String, val nameRes: StringResource) {
    Arabic(LANGUAGE_AR_ISO, Res.string.language_arabic),
    English(LANGUAGE_EN_ISO, Res.string.language_english);

    companion object {
        fun fromCode(code: String): AppLanguage = entries.firstOrNull { it.code == code } ?: Arabic
    }
}

/**
 * Where the app language is kept (LocaleHelper on Android). A new language shows once the UI
 * reloads, see PlatformAction.LanguageChanged.
 */
interface AppLanguageStore {
    fun language(): String
    fun setLanguage(code: String)
}
