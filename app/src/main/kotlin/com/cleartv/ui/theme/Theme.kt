package com.cleartv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.cleartv.data.model.AccentColor

/**
 * ClearTV colour tokens — provided via CompositionLocal so any
 * composable can access them without passing colours explicitly.
 */
data class ClearTVColors(
    val background: Color,
    val backgroundEnd: Color,
    val surface: Color,
    val surfaceBorder: Color,
    val glassSurface: Color,
    val glassBorder: Color,
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
    val accent: Color,          // pure accent (for swatches, links, etc.)
)

val LocalClearTVColors = staticCompositionLocalOf {
    buildPalette(darkTheme = false, accent = AccentColor.BLUE)
}

private fun buildPalette(darkTheme: Boolean, accent: AccentColor): ClearTVColors {
    val focusRing = accentFocusRing(accent)
    val blobBlue = accentBlobColor(accent)
    val accentPure = accentPureColor(accent)
    return if (darkTheme) {
        ClearTVColors(
            background = DarkColors.background,
            backgroundEnd = DarkColors.backgroundEnd,
            surface = DarkColors.surface,
            surfaceBorder = DarkColors.surfaceBorder,
            glassSurface = DarkColors.glassSurface,
            glassBorder = DarkColors.glassBorder,
            textPrimary = DarkColors.textPrimary,
            textSecondary = DarkColors.textSecondary,
            textTertiary = DarkColors.textTertiary,
            focusRing = focusRing,
            labelOverlay = DarkColors.labelOverlay,
            labelText = DarkColors.labelText,
            statusSurface = DarkColors.statusSurface,
            statusBorder = DarkColors.statusBorder,
            statusText = DarkColors.statusText,
            settingsTileBg = DarkColors.settingsTileBg,
            settingsTileFg = DarkColors.settingsTileFg,
            blobBlue = blobBlue,
            blobGreen = DarkColors.blobGreen,
            accent = accentPure,
        )
    } else {
        ClearTVColors(
            background = LightColors.background,
            backgroundEnd = LightColors.backgroundEnd,
            surface = LightColors.surface,
            surfaceBorder = LightColors.surfaceBorder,
            glassSurface = LightColors.glassSurface,
            glassBorder = LightColors.glassBorder,
            textPrimary = LightColors.textPrimary,
            textSecondary = LightColors.textSecondary,
            textTertiary = LightColors.textTertiary,
            focusRing = focusRing,
            labelOverlay = LightColors.labelOverlay,
            labelText = LightColors.labelText,
            statusSurface = LightColors.statusSurface,
            statusBorder = LightColors.statusBorder,
            statusText = LightColors.statusText,
            settingsTileBg = LightColors.settingsTileBg,
            settingsTileFg = LightColors.settingsTileFg,
            blobBlue = blobBlue,
            blobGreen = LightColors.blobGreen,
            accent = accentPure,
        )
    }
}

@Composable
fun ClearTVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: AccentColor = AccentColor.BLUE,
    content: @Composable () -> Unit,
) {
    val colors = buildPalette(darkTheme, accentColor)

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
