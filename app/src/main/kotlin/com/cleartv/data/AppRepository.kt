package com.cleartv.data

import android.content.Context
import android.content.pm.PackageManager
import com.cleartv.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Queries the device's PackageManager for launchable apps.
 * Filters out ClearTV itself and system apps without a launch intent.
 */
class AppRepository(private val context: Context) {

    /**
     * Returns all installed, launchable apps sorted alphabetically by label.
     * Runs on IO dispatcher to avoid blocking the main thread.
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != context.packageName }
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map { appInfo ->
                val flags = appInfo.flags
                val isSystem = (flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0 ||
                               (flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                AppInfo(
                    packageName = appInfo.packageName,
                    label = pm.getApplicationLabel(appInfo).toString(),
                    icon = pm.getApplicationIcon(appInfo.packageName),
                    isSystemApp = isSystem,
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    /**
     * Returns a launch intent for the given package, or null if not launchable.
     */
    fun getLaunchIntent(packageName: String) =
        context.packageManager.getLaunchIntentForPackage(packageName)
}
