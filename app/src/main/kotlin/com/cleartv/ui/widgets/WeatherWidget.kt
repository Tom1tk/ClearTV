package com.cleartv.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleartv.data.model.WeatherData
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.ui.util.softDropShadow
import kotlin.math.roundToInt

/**
 * Weather widget — current temperature + condition icon + 3-day forecast.
 *
 * Frosted glass card: blurred background gradient + semi-transparent white overlay
 * + white border. Simulates CSS backdrop-filter: blur(24px) saturate(1.8).
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
                .clip(shape),
        ) {
            FrostedGlassLayers(shape = shape, blurRadius = 24.dp)
            Box(modifier = Modifier.padding(14.dp, 20.dp)) {
                Text(
                    text = "Weather loading…",
                    style = ClearTVTypography.weatherCaption,
                    color = colors.textSecondary,
                )
            }
        }
        return
    }

    Box(
        modifier = modifier
            .softDropShadow()
            .clip(shape),
    ) {
        FrostedGlassLayers(shape = shape, blurRadius = 24.dp)

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
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

/**
 * Layered frosted-glass backdrop (must be called inside a Box content lambda):
 *   Layer 1 — blurred background gradient (matches screen background)
 *   Layer 2 — semi-transparent white overlay
 *   Layer 3 — white hairline border
 *
 * Since Compose's Modifier.blur() blurs the composable's own content (not a
 * true backdrop-filter), we reconstruct the screen gradient and blur it,
 * giving an approximation of frosted glass that works on all API levels.
 */
@Composable
private fun androidx.compose.foundation.layout.BoxScope.FrostedGlassLayers(
    shape: RoundedCornerShape,
    blurRadius: androidx.compose.ui.unit.Dp,
) {
    val colors = LocalClearTVColors.current

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
            .blur(blurRadius),
    )

    // Glass overlay — adapts to light/dark theme
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(colors.glassSurface),
    )

    // Border — adapts to light/dark theme
    Box(
        modifier = Modifier
            .matchParentSize()
            .border(1.dp, colors.glassBorder, shape),
    )
}
