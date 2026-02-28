package com.cleartv.ui.widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Large clock widget — HH:MM with a blinking colon and day/date below.
 * Updates every second. Uses the system clock (no network needed).
 */
@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current

    // Current time, updated every second
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val date = remember(currentTime) { Date(currentTime) }
    val hours = remember(currentTime) {
        SimpleDateFormat("HH", Locale.getDefault()).format(date)
    }
    val minutes = remember(currentTime) {
        SimpleDateFormat("mm", Locale.getDefault()).format(date)
    }
    val dateString = remember(currentTime) {
        SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(date)
    }

    // Blinking colon animation — fades between 1.0 and 0.0 every second
    val infiniteTransition = rememberInfiniteTransition(label = "clockBlink")
    val colonAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "colonAlpha",
    )

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier,
    ) {
        // Time — HH:MM
        Text(
            text = buildAnnotatedString {
                append(hours)
                withStyle(SpanStyle(color = colors.textPrimary.copy(alpha = colonAlpha))) {
                    append(":")
                }
                append(minutes)
            },
            style = ClearTVTypography.clock,
            color = colors.textPrimary,
        )

        // Date — "Fri 28 Feb"
        Text(
            text = dateString,
            style = ClearTVTypography.clockDate,
            color = colors.textSecondary,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}
