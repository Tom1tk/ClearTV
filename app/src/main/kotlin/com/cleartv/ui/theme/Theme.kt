package com.cleartv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * ClearTV colour tokens — provided via CompositionLocal so any
 * composable can access them without passing colours explicitly.
 */
data class ClearTVColors(
    val background: Color,
    val backgroundEnd: Color,
    val surface: Color,
    val surfaceBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val focusRing: Color,
    val labelOverlay: Color,
    val labelText: Color,
    val statusSurface: Color,
    val statusBorder: Color,
    val statusText: Color,
    val settingsTileBg: Color,
    val settingsTileFg: Color,
    val blobBlue: Color,
    val blobGreen: Color,
)

val LocalClearTVColors = staticCompositionLocalOf {
    ClearTVColors(
        background = LightColors.background,
        backgroundEnd = LightColors.backgroundEnd,
        surface = LightColors.surface,
        surfaceBorder = LightColors.surfaceBorder,
        textPrimary = LightColors.textPrimary,
        textSecondary = LightColors.textSecondary,
        textTertiary = LightColors.textTertiary,
        focusRing = LightColors.focusRing,
        labelOverlay = LightColors.labelOverlay,
        labelText = LightColors.labelText,
        statusSurface = LightColors.statusSurface,
        statusBorder = LightColors.statusBorder,
        statusText = LightColors.statusText,
        settingsTileBg = LightColors.settingsTileBg,
        settingsTileFg = LightColors.settingsTileFg,
        blobBlue = LightColors.blobBlue,
        blobGreen = LightColors.blobGreen,
    )
}

private val LightPalette = ClearTVColors(
    background = LightColors.background,
    backgroundEnd = LightColors.backgroundEnd,
    surface = LightColors.surface,
    surfaceBorder = LightColors.surfaceBorder,
    textPrimary = LightColors.textPrimary,
    textSecondary = LightColors.textSecondary,
    textTertiary = LightColors.textTertiary,
    focusRing = LightColors.focusRing,
    labelOverlay = LightColors.labelOverlay,
    labelText = LightColors.labelText,
    statusSurface = LightColors.statusSurface,
    statusBorder = LightColors.statusBorder,
    statusText = LightColors.statusText,
    settingsTileBg = LightColors.settingsTileBg,
    settingsTileFg = LightColors.settingsTileFg,
    blobBlue = LightColors.blobBlue,
    blobGreen = LightColors.blobGreen,
)

private val DarkPalette = ClearTVColors(
    background = DarkColors.background,
    backgroundEnd = DarkColors.backgroundEnd,
    surface = DarkColors.surface,
    surfaceBorder = DarkColors.surfaceBorder,
    textPrimary = DarkColors.textPrimary,
    textSecondary = DarkColors.textSecondary,
    textTertiary = DarkColors.textTertiary,
    focusRing = DarkColors.focusRing,
    labelOverlay = DarkColors.labelOverlay,
    labelText = DarkColors.labelText,
    statusSurface = DarkColors.statusSurface,
    statusBorder = DarkColors.statusBorder,
    statusText = DarkColors.statusText,
    settingsTileBg = DarkColors.settingsTileBg,
    settingsTileFg = DarkColors.settingsTileFg,
    blobBlue = DarkColors.blobBlue,
    blobGreen = DarkColors.blobGreen,
)

@Composable
fun ClearTVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkPalette else LightPalette

    // Also provide Material3 color scheme for any M3 components used
    val materialScheme = if (darkTheme) {
        darkColorScheme(
            background = colors.background,
            surface = colors.background,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
        )
    } else {
        lightColorScheme(
            background = colors.background,
            surface = colors.background,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
        )
    }

    CompositionLocalProvider(LocalClearTVColors provides colors) {
        MaterialTheme(
            colorScheme = materialScheme,
            content = content,
        )
    }
}
