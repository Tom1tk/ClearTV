package com.cleartv.ui.widgets

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleartv.data.model.WeatherData
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import kotlin.math.roundToInt

/**
 * Soft drop shadow matching the spec's CSS:
 *   box-shadow: 0 2px 20px rgba(0,0,0,0.06)
 *
 * Uses BlurMaskFilter instead of Modifier.shadow() to avoid the Material
 * elevation ring which renders as a harsh border on light backgrounds.
 */
private fun Modifier.softDropShadow(
    color: Color = Color(0x0F000000),   // rgba(0,0,0,0.06) ≈ 15/255 alpha
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
 * Weather widget — current temperature + condition icon + 3-day forecast.
 *
 * Frosted glass card aesthetic: semi-transparent white fill + soft CSS-spec
 * drop shadow. No Material shadow ring, no visible stroke border.
 */
@Composable
fun WeatherWidget(
    weather: WeatherData?,
    locationName: String = "",
    useCelsius: Boolean = true,
    showForecast: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current
    val shape = RoundedCornerShape(20.dp)

    if (weather == null) {
        Box(
            modifier = modifier
                .softDropShadow()
                .clip(shape)
                .background(colors.surface)
                .padding(14.dp, 20.dp),
        ) {
            Text(
                text = "Weather loading…",
                style = ClearTVTypography.weatherCaption,
                color = colors.textSecondary,
            )
        }
        return
    }

    Box(
        modifier = modifier
            .softDropShadow()
            .clip(shape)
            .background(colors.surface)
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Column {
            // Current temp + icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = weather.current.conditionIcon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${weather.current.temperature.roundToInt()}°",
                    style = ClearTVTypography.weatherTemp,
                    color = colors.textPrimary,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Location + condition
            Text(
                text = buildString {
                    if (locationName.isNotEmpty()) append("$locationName · ")
                    append(weather.current.conditionText)
                },
                style = ClearTVTypography.weatherCaption,
                color = colors.textTertiary,
            )

            // 3-day forecast strip
            if (showForecast && weather.forecast.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    weather.forecast.forEach { day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.dayName,
                                style = ClearTVTypography.weatherForecast,
                                color = colors.textSecondary,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = day.conditionIcon, fontSize = 14.sp)
                            Text(
                                text = "${day.high.roundToInt()}°",
                                style = ClearTVTypography.weatherForecast.copy(
                                    fontWeight = FontWeight.Medium,
                                ),
                                color = colors.textPrimary,
                            )
                            Text(
                                text = "${day.low.roundToInt()}°",
                                style = ClearTVTypography.weatherForecast,
                                color = colors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}
