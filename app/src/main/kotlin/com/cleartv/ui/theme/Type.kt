package com.cleartv.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography definitions for ClearTV.
 * Uses system sans-serif (Roboto on Fire OS) which maps well to the
 * SF Pro Display / Helvetica Neue reference in the spec.
 */
object ClearTVTypography {

    /** Large clock digits — HH:MM */
    val clock = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Thin,       // 200 weight
        fontSize = 52.sp,
        letterSpacing = (-2).sp,
    )

    /** Clock date line — "Fri 28 Feb" */
    val clockDate = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,     // 400
        fontSize = 13.sp,
    )

    /** Section headers — "FAVOURITES", "APPS" */
    val sectionHeader = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 11.sp,
        letterSpacing = 1.sp,
    )

    /** App tile label (large tiles) */
    val tileLabel = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,     // 500
        fontSize = 12.sp,
        letterSpacing = 0.1.sp,
    )

    /** App tile label (small tiles) */
    val tileLabelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.1.sp,
    )

    /** Status bar text */
    val status = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )

    /** Weather — current temperature */
    val weatherTemp = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,      // 300
        fontSize = 26.sp,
        letterSpacing = (-0.5).sp,
    )

    /** Weather — location/condition */
    val weatherCaption = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )

    /** Weather — forecast day labels */
    val weatherForecast = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
    )
}
