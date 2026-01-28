package com.ovasta.logisticsapp.base.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.ovasta.logisticsapp.R


fun Context.makePhoneCall(mobile: String?) {
    if (mobile.isNullOrEmpty()) {
        ToastHelper.showShortToaster(this, getString(R.string.not_available))
        return
    }
    val dialIntent = Intent(Intent.ACTION_DIAL)
    dialIntent.data = Uri.parse("tel:$mobile")
    startActivity(dialIntent)
}

fun Context.navigateToLocationClick(latitude: Float?, longitude: Float?) {
    if (latitude == null || longitude == null) {
        ToastHelper.showShortToaster(this, getString(R.string.not_available))
        return
    }
    val intentUri = Uri.parse("google.navigation:q=${latitude},${longitude}&mode=w")
    val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    try {
        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
    } catch (e: SecurityException) {
        ToastHelper.showShortToaster(applicationContext, getString(R.string.maps_error_pkg))
    } catch (e: Exception) {
        ToastHelper.showShortToaster(applicationContext, getString(R.string.maps_error_pkg))
    }
}

fun Context.copyPhoneNumber(mobile: String?) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label",mobile)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, getString(R.string.phone_number_copied), Toast.LENGTH_SHORT).show()
}
fun Context.openWhatsApp(phoneNumber: String?) {
    if (phoneNumber.isNullOrEmpty()) {
        Toast.makeText(this, getString(R.string.phone_number_not_available), Toast.LENGTH_SHORT).show()
        return
    }
    val uri = Uri.parse("https://wa.me/${phoneNumber.filter { it.isDigit() }}")
    val intent = Intent(Intent.ACTION_VIEW, uri)

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this,
            getString(R.string.whatsapp_is_not_installed_on_the_device), Toast.LENGTH_SHORT).show()
    }
}
