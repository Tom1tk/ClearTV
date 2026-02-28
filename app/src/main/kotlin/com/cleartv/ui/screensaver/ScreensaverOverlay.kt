package com.cleartv.ui.screensaver

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleartv.data.model.ScreensaverType
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screensaver overlay — rendered on top of the home screen.
 * Three modes: Dim, Clock, Slideshow.
 *
 * Not a separate Activity — avoids cold-start recomposition on wake.
 * Listens for any key/click event to dismiss.
 */
@Composable
fun ScreensaverOverlay(
    isActive: Boolean,
    screensaverType: ScreensaverType,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = isActive,
        enter = fadeIn(tween(2000)),
        exit = fadeOut(tween(300)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            when (screensaverType) {
                ScreensaverType.DIM -> DimScreensaver()
                ScreensaverType.CLOCK -> ClockScreensaver()
                ScreensaverType.SLIDESHOW -> SlideshowScreensaver()
            }
        }
    }
}

/**
 * Dim mode — full-screen 95% opaque black overlay.
 * Most effective burn-in mitigation for both OLED and LCD.
 */
@Composable
private fun DimScreensaver() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f)),
    )
}

/**
 * Clock mode — minimal white clock on black, slowly drifting position
 * to prevent static pixel burn. 60-second position cycle.
 */
@Composable
private fun ClockScreensaver() {
    // Drifting position — ±30px on a 60-second cycle
    val transition = rememberInfiniteTransition(label = "clockDrift")

    val offsetX by transition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(60_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "driftX",
    )

    val offsetY by transition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(45_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "driftY",
    )

    // Time, updated every second
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val date = remember(currentTime) { Date(currentTime) }
    val timeString = remember(currentTime) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = timeString,
            fontSize = 72.sp,
            fontWeight = FontWeight.Thin,
            color = Color.White,
            letterSpacing = (-2).sp,
            modifier = Modifier.offset(x = offsetX.dp, y = offsetY.dp),
        )
    }
}

/**
 * Slideshow mode — placeholder implementation.
 * In production, reads images from a user-selected folder.
 * For now, shows a gentle colour gradient that shifts slowly.
 */
@Composable
private fun SlideshowScreensaver() {
    // Phase 3: basic placeholder — full slideshow needs file picker (Phase 4)
    val transition = rememberInfiniteTransition(label = "slideshowHue")
    val hueShift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(120_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "hue",
    )

    // Simple dim display with subtle colour
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "📷",
            fontSize = 48.sp,
            color = Color.White.copy(alpha = 0.15f),
        )
    }
}
