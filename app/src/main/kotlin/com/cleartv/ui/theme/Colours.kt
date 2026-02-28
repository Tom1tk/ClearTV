package com.cleartv.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Light Palette ──────────────────────────────────────────────────────────
object LightColors {
    val background = Color(0xFFF2F2F7)
    val backgroundEnd = Color(0xFFE8E8ED)
    val surface = Color(0x8CFFFFFF)          // rgba(255,255,255,0.55)
    val surfaceBorder = Color(0xCCFFFFFF)    // rgba(255,255,255,0.8)
    val textPrimary = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF8E8E93)
    val textTertiary = Color(0xFF636366)
    val focusRing = Color(0x8C007AFF)        // rgba(0,122,255,0.55)
    val labelOverlay = Color(0x40000000)     // rgba(0,0,0,0.25)
    val labelText = Color(0xE6FFFFFF)        // rgba(255,255,255,0.9)
    val statusSurface = Color(0x80FFFFFF)    // rgba(255,255,255,0.5)
    val statusBorder = Color(0xB3FFFFFF)     // rgba(255,255,255,0.7)
    val statusText = Color(0xFF3A3A3C)
    val settingsTileBg = Color(0xFFE0E0E5)
    val settingsTileFg = Color(0xFF3A3A3C)

    // Subtle background blobs
    val blobBlue = Color(0x12007AFF)         // rgba(0,122,255,0.07)
    val blobGreen = Color(0x0F34C759)        // rgba(52,199,89,0.06)
}

// ─── Dark Palette (stubbed) ─────────────────────────────────────────────────
object DarkColors {
    val background = Color(0xFF0A0A0F)
    val backgroundEnd = Color(0xFF0A0A0F)
    val surface = Color(0x0AFFFFFF)           // rgba(255,255,255,0.04)
    val surfaceBorder = Color(0x14FFFFFF)     // rgba(255,255,255,0.08)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0x8CFFFFFF)     // rgba(255,255,255,0.55)
    val textTertiary = Color(0x8CFFFFFF)
    val focusRing = Color(0x8C007AFF)         // same in both modes
    val labelOverlay = Color(0x40000000)
    val labelText = Color(0xE6FFFFFF)
    val statusSurface = Color(0xB31E1E23)    // rgba(30,30,35,0.7)
    val statusBorder = Color(0x14FFFFFF)
    val statusText = Color(0xFFFFFFFF)
    val settingsTileBg = Color(0xFF1E1E23)
    val settingsTileFg = Color(0xFFFFFFFF)

    val blobBlue = Color(0x0A007AFF)
    val blobGreen = Color(0x0A34C759)
}
