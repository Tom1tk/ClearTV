package com.cleartv.data.model

import android.graphics.drawable.Drawable

/**
 * Represents an installed app on the device.
 *
 * @param packageName The app's package name used for launching
 * @param label Human-readable app name
 * @param icon The app's launcher icon drawable
 */
data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val isSystemApp: Boolean = false,
)
