package com.cleartv.ui.settings

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cleartv.data.model.ScreensaverType
import com.cleartv.data.model.ThemeMode
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.util.IntentUtil
import kotlinx.coroutines.launch

/**
 * Full settings panel — Appearance, Screensaver, Apps, Weather, About.
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
                    end = Offset.Infinite,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 80.dp, vertical = 48.dp),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "← Back",
                    style = ClearTVTypography.status,
                    color = colors.focusRing,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateBack() }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
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

            // ═══ APPEARANCE ═══
            SectionHeader("Appearance")

            SettingsCard {
                Text("Theme", style = settingsLabel(), color = colors.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeChip("Light", prefs.theme == ThemeMode.LIGHT) { viewModel.setTheme(ThemeMode.LIGHT) }
                    ThemeChip("Dark", prefs.theme == ThemeMode.DARK) { viewModel.setTheme(ThemeMode.DARK) }
                    ThemeChip("System", prefs.theme == ThemeMode.SYSTEM) { viewModel.setTheme(ThemeMode.SYSTEM) }
                }
            }

            SettingsCard {
                Text("Blur Intensity", style = settingsLabel(), color = colors.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeChip("Low", prefs.blurIntensity == 0) { viewModel.setBlurIntensity(0) }
                    ThemeChip("Medium", prefs.blurIntensity == 1) { viewModel.setBlurIntensity(1) }
                    ThemeChip("High", prefs.blurIntensity == 2) { viewModel.setBlurIntensity(2) }
                }
            }

            SettingsToggle("Show Clock", prefs.showClock) { viewModel.setShowClock(it) }
            SettingsToggle("Show Weather Widget", prefs.showWeather) { viewModel.setShowWeather(it) }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══ SCREENSAVER ═══
            SectionHeader("Screensaver")

            SettingsToggle("Enable Screensaver", prefs.screensaverEnabled) {
                viewModel.setScreensaverEnabled(it)
            }

            if (prefs.screensaverEnabled) {
                SettingsCard {
                    Text("Idle Timeout", style = settingsLabel(), color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 15, 30, 60).forEach { min ->
                            ThemeChip(
                                label = "${min}m",
                                selected = prefs.screensaverTimeoutMin == min,
                            ) { viewModel.setScreensaverTimeout(min) }
                        }
                    }
                }

                SettingsCard {
                    Text("Screensaver Type", style = settingsLabel(), color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeChip("Dim", prefs.screensaverType == ScreensaverType.DIM) {
                            viewModel.setScreensaverType(ScreensaverType.DIM)
                        }
                        ThemeChip("Clock", prefs.screensaverType == ScreensaverType.CLOCK) {
                            viewModel.setScreensaverType(ScreensaverType.CLOCK)
                        }
                        ThemeChip("Slideshow", prefs.screensaverType == ScreensaverType.SLIDESHOW) {
                            viewModel.setScreensaverType(ScreensaverType.SLIDESHOW)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══ WEATHER ═══
            SectionHeader("Weather")

            SettingsCard {
                Text("Location", style = settingsLabel(), color = colors.textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = prefs.weatherLocation.ifEmpty { "Auto (default: London)" },
                    style = ClearTVTypography.tileLabelSmall,
                    color = colors.textSecondary,
                )
            }

            SettingsCard {
                Text("Units", style = settingsLabel(), color = colors.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeChip("°C", prefs.weatherCelsius) { viewModel.setWeatherCelsius(true) }
                    ThemeChip("°F", !prefs.weatherCelsius) { viewModel.setWeatherCelsius(false) }
                }
            }

            SettingsCard {
                Text("Time Format", style = settingsLabel(), color = colors.textPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeChip("12-hour", prefs.weather12hr) { viewModel.setWeather12hr(true) }
                    ThemeChip("24-hour", !prefs.weather12hr) { viewModel.setWeather12hr(false) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══ APPS ═══
            SectionHeader("Apps")

            if (favouriteApps.isNotEmpty()) {
                SettingsCard {
                    Text("Favourites", style = settingsLabel(), color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    favouriteApps.forEach { app ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                app.label,
                                style = ClearTVTypography.status,
                                color = colors.textPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                "Remove",
                                style = ClearTVTypography.tileLabelSmall,
                                color = colors.focusRing,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { viewModel.removeFavourite(app.packageName) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            if (hiddenApps.isNotEmpty()) {
                SettingsCard {
                    Text("Hidden Apps", style = settingsLabel(), color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    hiddenApps.forEach { app ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                app.label,
                                style = ClearTVTypography.status,
                                color = colors.textPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                "Restore",
                                style = ClearTVTypography.tileLabelSmall,
                                color = colors.focusRing,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { viewModel.unhideApp(app.packageName) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            SettingsToggle("Show System Apps", prefs.showSystemApps) { viewModel.setShowSystemApps(it) }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══ ABOUT & SYSTEM ═══
            SectionHeader("About & System")

            SettingsCard {
                SettingsLink("Open Fire OS Settings") { context.startActivity(IntentUtil.systemSettings()) }
                Spacer(modifier = Modifier.height(8.dp))
                SettingsLink("Open Display Settings") { context.startActivity(IntentUtil.displaySettings()) }
                Spacer(modifier = Modifier.height(8.dp))
                SettingsLink("Open Network Settings") { context.startActivity(IntentUtil.wifiSettings()) }
            }

            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("App Version", style = settingsLabel(), color = colors.textPrimary)
                    Text("1.0.0", style = ClearTVTypography.status, color = colors.textSecondary)
                }
            }

            SettingsCard {
                Text(
                    "Restore Defaults",
                    style = settingsLabel(),
                    color = colors.focusRing,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.restoreDefaults() }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ─── Reusable components ─────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    val colors = LocalClearTVColors.current
    Text(
        title.uppercase(),
        style = ClearTVTypography.sectionHeader,
        color = colors.textSecondary,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

/**
 * Card container for a settings group.
 *
 * Scroll-ahead: uses BringIntoViewRequester with a rect that extends
 * [LOOKAHEAD_DP] below the card's own bounds. When any child receives
 * focus, the verticalScroll container scrolls to show the card plus the
 * next item below it — the user always sees what's coming next without
 * having to press Down first.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    val colors = LocalClearTVColors.current
    val requester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var heightPx by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .bringIntoViewRequester(requester)
            .onSizeChanged { heightPx = it.height }
            .onFocusChanged { focusState ->
                if (focusState.hasFocus) {
                    scope.launch {
                        val lookahead = with(density) { LOOKAHEAD_DP.toPx() }
                        requester.bringIntoView(
                            Rect(
                                left = 0f,
                                top = -lookahead,
                                right = Float.MAX_VALUE,
                                bottom = heightPx + lookahead,
                            )
                        )
                    }
                }
            }
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.surfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) { Column { content() } }
}

private val LOOKAHEAD_DP = 160.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SettingsToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
private fun ThemeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalClearTVColors.current
    Text(
        label,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (selected) colors.focusRing else colors.textSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) colors.focusRing.copy(alpha = 0.12f) else colors.surface)
            .border(1.dp, if (selected) colors.focusRing.copy(alpha = 0.3f) else colors.surfaceBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/**
 * A tappable/focusable link row inside a SettingsCard.
 * clip() before clickable() ensures the focus ripple is rounded.
 */
@Composable
private fun SettingsLink(label: String, onClick: () -> Unit) {
    val colors = LocalClearTVColors.current
    Text(
        label,
        style = settingsLabel(),
        color = colors.focusRing,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
    )
}

@Composable
private fun settingsLabel() = ClearTVTypography.status.copy(fontWeight = FontWeight.Medium)
