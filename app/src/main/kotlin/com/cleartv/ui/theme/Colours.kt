package com.cleartv.ui.theme

import androidx.compose.ui.graphics.Color
import com.cleartv.data.model.AccentColor

// ─── Accent Color Palette ────────────────────────────────────────────────────

/** Pure (opaque) accent color for swatches and previews. */
fun accentPureColor(accent: AccentColor): Color = when (accent) {
    AccentColor.BLUE   -> Color(0xFF007AFF)
    AccentColor.GREEN  -> Color(0xFF34C759)
    AccentColor.RED    -> Color(0xFFFF3B30)
    AccentColor.ORANGE -> Color(0xFFFF9500)
    AccentColor.PINK   -> Color(0xFFFF2D55)
    AccentColor.PURPLE -> Color(0xFFAF52DE)
}

/** Focus-ring variant: accent at 55% alpha (matches CSS rgba spec). */
fun accentFocusRing(accent: AccentColor): Color =
    accentPureColor(accent).copy(alpha = 0.55f)

/** Subtle blob tint variant: accent at 7% alpha. */
fun accentBlobColor(accent: AccentColor): Color =
    accentPureColor(accent).copy(alpha = 0.07f)

// ─── Light Palette ──────────────────────────────────────────────────────────
object LightColors {
    val background = Color(0xFFF2F2F7)
    val backgroundEnd = Color(0xFFE8E8ED)
    val surface = Color(0x8CFFFFFF)          // rgba(255,255,255,0.55) — tile bg
    val surfaceBorder = Color(0x40FFFFFF)    // rgba(255,255,255,0.25)
    val glassSurface = Color(0x8CFFFFFF)     // frosted glass widget overlay (55%)
    val glassBorder = Color(0xCCFFFFFF)      // frosted glass widget border (80%)
    val textPrimary = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF8E8E93)
    val textTertiary = Color(0xFF636366)
    val labelOverlay = Color(0x40000000)     // rgba(0,0,0,0.25)
    val labelText = Color(0xE6FFFFFF)        // rgba(255,255,255,0.9)
    val statusSurface = Color(0x80FFFFFF)    // rgba(255,255,255,0.5)
    val statusBorder = Color(0xB3FFFFFF)     // rgba(255,255,255,0.7)
    val statusText = Color(0xFF3A3A3C)
    val settingsTileBg = Color(0xFFE0E0E5)
    val settingsTileFg = Color(0xFF3A3A3C)
    val blobGreen = Color(0x0F34C759)        // rgba(52,199,89,0.06)
}

// ─── Dark Palette ────────────────────────────────────────────────────────────
object DarkColors {
    val background = Color(0xFF0A0A0F)
    val backgroundEnd = Color(0xFF0A0A0F)
    val surface = Color(0x1AFFFFFF)          // rgba(255,255,255,0.10) — tile bg
    val surfaceBorder = Color(0x14FFFFFF)    // rgba(255,255,255,0.08)
    val glassSurface = Color(0x1AFFFFFF)     // frosted glass widget overlay (10%)
    val glassBorder = Color(0x33FFFFFF)      // frosted glass widget border (20%)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0x8CFFFFFF)    // rgba(255,255,255,0.55)
    val textTertiary = Color(0x8CFFFFFF)
    val labelOverlay = Color(0x40000000)
    val labelText = Color(0xE6FFFFFF)
    val statusSurface = Color(0x26FFFFFF)    // rgba(255,255,255,0.15)
    val statusBorder = Color(0x33FFFFFF)     // rgba(255,255,255,0.20)
    val statusText = Color(0xFFFFFFFF)
    val settingsTileBg = Color(0xFF1E1E23)
    val settingsTileFg = Color(0xFFFFFFFF)
    val blobGreen = Color(0x0A34C759)
}
