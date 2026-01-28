package com.ovasta.logisticsapp.base.ext

import android.content.Context
import android.widget.Toast


object ToastHelper {
    fun showLongToaster(context: Context?, message: String) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun showShortToaster(context: Context?, message: String?) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
