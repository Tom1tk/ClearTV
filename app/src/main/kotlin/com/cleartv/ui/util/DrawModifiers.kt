package com.cleartv.ui.util

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Soft drop shadow using BlurMaskFilter — avoids the Material elevation ring
 * which renders as a harsh border on light backgrounds.
 *
 * CSS equivalent: box-shadow: 0 {offsetY} {blur} {color}
 */
fun Modifier.softDropShadow(
    color: Color = Color(0x0F000000),   // rgba(0,0,0,0.06) ≈ 15/255
    blur: Dp = 20.dp,
    offsetY: Dp = 2.dp,
    cornerRadius: Dp = 20.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = color.toArgb()
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }
        canvas.drawRoundRect(
            left = 0f,
            top = offsetY.toPx(),
            right = size.width,
            bottom = size.height + offsetY.toPx(),
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint,
        )
    }
}

/**
 * Resting tile shadow: 0 2px 12px rgba(0,0,0,0.08)
 */
fun Modifier.tileRestingShadow(cornerRadius: Dp = 16.dp): Modifier =
    softDropShadow(
        color = Color(0x14000000),
        blur = 12.dp,
        offsetY = 2.dp,
        cornerRadius = cornerRadius,
    )

/**
 * Focused tile shadow: 0 12px 40px rgba(0,0,0,0.18)
 */
fun Modifier.tileFocusedShadow(cornerRadius: Dp = 16.dp): Modifier =
    softDropShadow(
        color = Color(0x2E000000),
        blur = 40.dp,
        offsetY = 12.dp,
        cornerRadius = cornerRadius,
    )

/**
 * Outer focus ring drawn OUTSIDE the composable's own bounds via drawBehind.
 * CSS equivalent: box-shadow: 0 0 0 {ringWidth} {color}
 *
 * Because this uses drawBehind (before clip in the chain), it draws outside
 * the clipped area. The parent must provide sufficient layout padding for the
 * ring to not be clipped at the viewport boundary.
 */
fun Modifier.outerFocusRing(
    color: Color,
    ringWidth: Dp = 3.dp,
    cornerRadius: Dp = 16.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = ringWidth.toPx()
                this.color = color.toArgb()
            }
        }
        val half = ringWidth.toPx() / 2f
        canvas.drawRoundRect(
            left = -half,
            top = -half,
            right = size.width + half,
            bottom = size.height + half,
            radiusX = cornerRadius.toPx() + half,
            radiusY = cornerRadius.toPx() + half,
            paint = paint,
        )
    }
}
