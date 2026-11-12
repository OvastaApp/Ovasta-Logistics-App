package com.ovasta.logisticsapp.base.local

import android.app.Application


class AppResources(private val application: Application) {
    // Resolved on each call, since the agent can change the language while the app runs
    fun getString(resId: Int): String =
        LocaleHelper.localizedContext(application).getString(resId)
}