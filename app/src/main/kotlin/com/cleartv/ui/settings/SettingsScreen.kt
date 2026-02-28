package com.cleartv.ui.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cleartv.data.model.ThemeMode
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.util.IntentUtil

/**
 * Full settings panel — Appearance, Apps, About sections.
 * Navigated to via the ⚙ tile or Menu button.
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val colors = LocalClearTVColors.current
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val favouriteApps by viewModel.favouriteApps.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colors.background, colors.backgroundEnd),
                    start = Offset(0f, 0f),
                    end = Offset(Float.MAX_VALUE, Float.MAX_VALUE),
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 80.dp, vertical = 40.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "← Back",
                    style = ClearTVTypography.status,
                    color = colors.focusRing,
                    modifier = Modifier.clickable { onNavigateBack() },
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = colors.textPrimary,
                    letterSpacing = (-0.5).sp,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ═══════════════════════════════════════════════════
            //  APPEARANCE
            // ═══════════════════════════════════════════════════
            SectionHeader("Appearance")

            // Theme toggle
            SettingsCard {
                Text("Theme", style = settingsLabel(), color = colors.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeChip("Light", prefs.theme == ThemeMode.LIGHT) {
                        viewModel.setTheme(ThemeMode.LIGHT)
                    }
                    ThemeChip("Dark", prefs.theme == ThemeMode.DARK) {
                        viewModel.setTheme(ThemeMode.DARK)
                    }
                    ThemeChip("System", prefs.theme == ThemeMode.SYSTEM) {
                        viewModel.setTheme(ThemeMode.SYSTEM)
                    }
                }
            }

            // Blur intensity
            SettingsCard {
                Text("Blur Intensity", style = settingsLabel(), color = colors.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeChip("Low", prefs.blurIntensity == 0) { viewModel.setBlurIntensity(0) }
                    ThemeChip("Medium", prefs.blurIntensity == 1) { viewModel.setBlurIntensity(1) }
                    ThemeChip("High", prefs.blurIntensity == 2) { viewModel.setBlurIntensity(2) }
                }
            }

            // Show clock
            SettingsToggle("Show Clock", prefs.showClock) { viewModel.setShowClock(it) }

            // Show weather
            SettingsToggle("Show Weather Widget", prefs.showWeather) { viewModel.setShowWeather(it) }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════════════════════════════════════════
            //  APPS
            // ═══════════════════════════════════════════════════
            SectionHeader("Apps")

            // Manage favourites
            if (favouriteApps.isNotEmpty()) {
                SettingsCard {
                    Text("Favourites", style = settingsLabel(), color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    favouriteApps.forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = app.label,
                                style = ClearTVTypography.status,
                                color = colors.textPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "Remove",
                                style = ClearTVTypography.tileLabelSmall,
                                color = colors.focusRing,
                                modifier = Modifier.clickable {
                                    viewModel.removeFavourite(app.packageName)
                                },
                            )
                        }
                    }
                }
            }

            // Hidden apps
            if (hiddenApps.isNotEmpty()) {
                SettingsCard {
                    Text("Hidden Apps", style = settingsLabel(), color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    hiddenApps.forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = app.label,
                                style = ClearTVTypography.status,
                                color = colors.textPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "Restore",
                                style = ClearTVTypography.tileLabelSmall,
                                color = colors.focusRing,
                                modifier = Modifier.clickable {
                                    viewModel.unhideApp(app.packageName)
                                },
                            )
                        }
                    }
                }
            }

            // Show system apps
            SettingsToggle("Show System Apps", prefs.showSystemApps) {
                viewModel.setShowSystemApps(it)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════════════════════════════════════════
            //  ABOUT & SYSTEM
            // ═══════════════════════════════════════════════════
            SectionHeader("About & System")

            SettingsCard {
                SettingsLink("Open Fire OS Settings") {
                    context.startActivity(IntentUtil.systemSettings())
                }
                Spacer(modifier = Modifier.height(8.dp))
                SettingsLink("Open Display Settings") {
                    context.startActivity(IntentUtil.displaySettings())
                }
                Spacer(modifier = Modifier.height(8.dp))
                SettingsLink("Open Network Settings") {
                    context.startActivity(IntentUtil.wifiSettings())
                }
            }

            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("App Version", style = settingsLabel(), color = colors.textPrimary)
                    Text("0.2.0", style = ClearTVTypography.status, color = colors.textSecondary)
                }
            }

            SettingsCard {
                Text(
                    text = "Restore Defaults",
                    style = settingsLabel(),
                    color = colors.focusRing,
                    modifier = Modifier.clickable { viewModel.restoreDefaults() },
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ─── Reusable settings components ────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    val colors = LocalClearTVColors.current
    Text(
        text = title.uppercase(),
        style = ClearTVTypography.sectionHeader,
        color = colors.textSecondary,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit,
) {
    val colors = LocalClearTVColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.surfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = LocalClearTVColors.current
    SettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = settingsLabel(), color = colors.textPrimary)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.focusRing,
                    checkedTrackColor = colors.focusRing.copy(alpha = 0.3f),
                ),
            )
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalClearTVColors.current
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (selected) colors.focusRing else colors.textSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) colors.focusRing.copy(alpha = 0.12f)
                else colors.surface
            )
            .border(
                1.dp,
                if (selected) colors.focusRing.copy(alpha = 0.3f)
                else colors.surfaceBorder,
                RoundedCornerShape(12.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun SettingsLink(
    label: String,
    onClick: () -> Unit,
) {
    val colors = LocalClearTVColors.current
    Text(
        text = label,
        style = settingsLabel(),
        color = colors.focusRing,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
    )
}

@Composable
private fun settingsLabel() = ClearTVTypography.status.copy(
    fontWeight = FontWeight.Medium,
)
