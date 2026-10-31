package com.ovasta.logisticsapp.base.local

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.content.edit
import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.base.constants.SharedPreferenceConstants.USER_LANGUAGE_KEY
import java.util.Locale

/**
 * Runs the app in the language the agent picked in Profile (Arabic by default),
 * regardless of the device language.
 *
 * Applied via [android.content.ContextWrapper.attachBaseContext] in both the
 * Application and the Activities so every resource lookup resolves against the
 * chosen language. The choice is kept in plain SharedPreferences because
 * attachBaseContext runs before Koin and the DataStore are available.
 */
object LocaleHelper {

    private const val PREFERENCES_NAME = "locale_preferences"

    fun getLanguage(context: Context): String =
        preferences(context).getString(USER_LANGUAGE_KEY, null) ?: LANGUAGE_AR_ISO

    /** Saves [language]; screens show it once the Activity is recreated. */
    fun setLanguage(context: Context, language: String) {
        preferences(context).edit { putString(USER_LANGUAGE_KEY, language) }
    }

    fun applySavedLocale(context: Context): Context {
        Locale.setDefault(Locale.forLanguageTag(getLanguage(context)))
        return localizedContext(context)
    }

    /** A copy of [context] whose resources resolve against the saved language. */
    fun localizedContext(context: Context): Context {
        val locale = Locale.forLanguageTag(getLanguage(context))
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    private fun preferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
}
