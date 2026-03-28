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

fun Context.navigateToLocationClick(latitude: Double?, longitude: Double?) {

    if (latitude == null || longitude == null) {
        ToastHelper.showShortToaster(this, getString(R.string.not_available))
        return
    }

    val uriGoogleNav = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
    val uriGeo = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")

    try {
        val navIntent = Intent(Intent.ACTION_VIEW, uriGoogleNav).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(navIntent)

    } catch (e: Exception) {

        try {
            val geoIntent = Intent(Intent.ACTION_VIEW, uriGeo)
            startActivity(geoIntent)

        } catch (e: Exception) {

            try {
                // 3️⃣ Fallback: browser (always works)
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")
                )
                startActivity(webIntent)

            } catch (e: Exception) {
                ToastHelper.showShortToaster(
                    this,
                    getString(R.string.maps_error_pkg)
                )
            }
        }
    }
}

fun Context.openWhatsApp(phoneNumber: String?) {

    if (phoneNumber.isNullOrEmpty()) {
        Toast.makeText(
            this,
            getString(R.string.phone_number_not_available),
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val cleanedNumber = phoneNumber.filter { it.isDigit() }

    if (cleanedNumber.isEmpty()) {
        Toast.makeText(
            this,
            getString(R.string.phone_number_not_available),
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val uri = Uri.parse("https://wa.me/$cleanedNumber")

    try {
        // Try normal intent FIRST (no package restriction)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)

    } catch (e: Exception) {
        // Fallback to WhatsApp explicitly
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp")
            }
            startActivity(intent)

        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.whatsapp.w4b")
                }
                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.whatsapp_is_not_installed_on_the_device),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
