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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleartv.data.model.WeatherData
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import kotlin.math.roundToInt

/**
 * Weather widget — current temperature + condition icon + 3-day forecast.
 * Matches the frosted glass card aesthetic from the mockup.
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

    if (weather == null) {
        // Loading / unavailable state
        Box(
            modifier = modifier
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.surfaceBorder, RoundedCornerShape(20.dp))
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
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.surfaceBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Column {
            // Current temp + icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = weather.current.conditionIcon,
                    fontSize = 28.sp,
                )
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    weather.forecast.forEach { day ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = day.dayName,
                                style = ClearTVTypography.weatherForecast,
                                color = colors.textSecondary,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = day.conditionIcon,
                                fontSize = 14.sp,
                            )
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
