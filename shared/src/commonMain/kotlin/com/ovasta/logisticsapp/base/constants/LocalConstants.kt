package com.ovasta.logisticsapp.base.constants

object LocalConstants {
    const val AppId = "com.ovasta.logisticsapp"
    const val KEY_APP_LANGUAGE = "KEY_APP_LANGUAGE"
    const val COMM_PROTOCOL = "https://"
    const val LANGUAGE_EN_ISO = "en"
    const val LANGUAGE_AR_ISO = "ar"
    const val DELAY_CLICK_ACTION = 500L

    // Assigned (app) orders are view-only in this release: no status change, pick, edit or delete.
    // Flip to true to re-enable the order/product actions.
    const val ORDER_ACTIONS_ENABLED = false

}