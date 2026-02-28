package com.cleartv.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Listens for app install, uninstall, and update broadcasts.
 * Triggers a callback so the home screen can refresh its app list
 * without requiring a full relaunch.
 */
class AppInstallReceiver(
    private val onAppChanged: () -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_CHANGED -> {
                onAppChanged()
            }
        }
    }

    companion object {
        /**
         * Creates an IntentFilter covering all relevant package events.
         */
        fun createFilter(): IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
    }
}
