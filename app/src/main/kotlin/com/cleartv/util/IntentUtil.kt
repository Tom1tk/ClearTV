package com.cleartv.util

import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Helper object for creating system settings intents.
 * Fire OS respects all standard android.provider.Settings action strings.
 */
object IntentUtil {
    fun systemSettings(): Intent = Intent(Settings.ACTION_SETTINGS)
    fun wifiSettings(): Intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    fun displaySettings(): Intent = Intent(Settings.ACTION_DISPLAY_SETTINGS)

    fun appSettings(packageName: String): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
}
