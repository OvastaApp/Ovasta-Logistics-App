package com.ovasta.logisticsapp.base.local

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Forces the app to run in Arabic regardless of the device language.
 *
 * Applied via [android.content.ContextWrapper.attachBaseContext] in both the
 * Application and the Activities so every resource lookup resolves against the
 * Arabic configuration.
 */
object LocaleHelper {

    private const val DEFAULT_LANGUAGE = "ar"

    fun applyDefaultLocale(context: Context): Context {
        val locale = Locale.forLanguageTag(DEFAULT_LANGUAGE)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}
