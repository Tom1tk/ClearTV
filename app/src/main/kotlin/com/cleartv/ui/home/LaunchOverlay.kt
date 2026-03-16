package com.cleartv.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors

/**
 * Full-screen frosted overlay shown while an app is launching.
 *
 * Fade-in + scale 0.97→1.0 animation over 200ms.
 * Tapping anywhere or pressing Back dismisses the overlay.
 */
@Composable
fun LaunchOverlay(
    app: AppInfo,
    onDismiss: () -> Unit,
) {
    val colors = LocalClearTVColors.current

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(200)) + scaleIn(initialScale = 0.97f, animationSpec = tween(200)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
                .background(Color(0xB8F2F2F7))   // rgba(242,242,247,0.72)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AsyncImage(
                    model = app.icon,
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = app.label,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.3).sp,
                    color = colors.textPrimary,
                )
                Text(
                    text = "Launching…",
                    style = ClearTVTypography.weatherCaption.copy(
                        fontSize = 13.sp,
                        letterSpacing = 0.06.sp,
                    ),
                    color = colors.textSecondary,
                )
            }
        }
    }
}
