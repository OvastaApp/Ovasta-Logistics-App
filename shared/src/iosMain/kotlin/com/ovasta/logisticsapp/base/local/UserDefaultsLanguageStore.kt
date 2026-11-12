package com.ovasta.logisticsapp.base.local

import com.ovasta.logisticsapp.base.constants.LocalConstants.LANGUAGE_AR_ISO
import com.ovasta.logisticsapp.base.constants.SharedPreferenceConstants.USER_LANGUAGE_KEY
import platform.Foundation.NSUserDefaults

/**
 * The app language (Arabic by default), whatever the device language. Compose resources read
 * "AppleLanguages", so it is set too; the new language shows once ContentView recreates the
 * Compose view (PlatformAction.LanguageChanged).
 */
class UserDefaultsLanguageStore : AppLanguageStore {

    override fun language(): String = savedAppLanguage()

    override fun setLanguage(code: String) {
        NSUserDefaults.standardUserDefaults.setObject(code, forKey = USER_LANGUAGE_KEY)
        applyAppLanguage(code)
    }
}

fun savedAppLanguage(): String =
    NSUserDefaults.standardUserDefaults.stringForKey(USER_LANGUAGE_KEY) ?: LANGUAGE_AR_ISO

/** Makes Compose resources resolve strings in [code]; call before the Compose view is built. */
fun applyAppLanguage(code: String = savedAppLanguage()) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(code), forKey = "AppleLanguages")
}
