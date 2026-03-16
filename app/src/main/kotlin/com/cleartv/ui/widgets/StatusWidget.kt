package com.cleartv.ui.widgets

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.ui.util.softDropShadow

/**
 * Status widget — shows real WiFi signal strength + device name.
 * Tapping navigates to system settings.
 * Frosted glass card: simulates CSS backdrop-filter: blur(12px).
 */
@Composable
fun StatusWidget(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current
    val context = LocalContext.current
    val shape = RoundedCornerShape(20.dp)

    val wifiStatus = remember { getWifiStatus(context) }

    Box(
        modifier = modifier
            .softDropShadow(
                color = Color(0x0D000000),
                blur = 8.dp,
                offsetY = 1.dp,
                cornerRadius = 20.dp,
            )
            .clip(shape)
            .clickable { onClick() },
    ) {
        // Blurred gradient backdrop
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(colors.background, colors.backgroundEnd),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    )
                )
                .blur(12.dp),
        )

        // Glass overlay — adapts to light/dark theme
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(colors.statusSurface),
        )

        // Border — adapts to light/dark theme
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(1.dp, colors.statusBorder, shape),
        )

        // Content row
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = wifiStatus,
                style = ClearTVTypography.status,
                color = colors.statusText,
            )
            Text(
                text = "|",
                style = ClearTVTypography.status,
                color = colors.statusText.copy(alpha = 0.3f),
            )
            Text(
                text = android.os.Build.MODEL,
                style = ClearTVTypography.status,
                color = colors.statusText,
            )
        }
    }
}

/**
 * Reads the current WiFi signal strength from the system.
 */
private fun getWifiStatus(context: Context): String {
    try {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return "WiFi ▸ Unknown"

        val network = connectivityManager.activeNetwork ?: return "No WiFi"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "No WiFi"

        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return "No WiFi"
        }

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return "WiFi ▸ Connected"

        @Suppress("DEPRECATION")
        val rssi = wifiManager.connectionInfo.rssi
        val level = WifiManager.calculateSignalLevel(rssi, 4)

        return when (level) {
            0 -> "WiFi ▸ Weak"
            1 -> "WiFi ▸ Fair"
            2 -> "WiFi ▸ Good"
            3 -> "WiFi ▸ Strong"
            else -> "WiFi ▸ Connected"
        }
    } catch (_: Exception) {
        return "WiFi ▸ Unknown"
    }
}
