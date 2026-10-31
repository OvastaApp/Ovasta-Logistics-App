package com.ovasta.logisticsapp.base.local

import androidx.annotation.StringRes
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_EN_ISO

/** Languages the agent can pick in Profile. */
enum class AppLanguage(val code: String, @StringRes val nameRes: Int) {
    Arabic(LANGUAGE_AR_ISO, R.string.language_arabic),
    English(LANGUAGE_EN_ISO, R.string.language_english);

    companion object {
        fun fromCode(code: String): AppLanguage = entries.firstOrNull { it.code == code } ?: Arabic
    }
}
