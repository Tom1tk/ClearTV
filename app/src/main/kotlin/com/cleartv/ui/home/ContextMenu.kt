package com.cleartv.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.LocalClearTVColors

/**
 * Long-press context menu overlay.
 * Shows actions: Pin/Unpin favourite, Hide app.
 * Rendered as a full-screen scrim with a centered card.
 */
@Composable
fun ContextMenu(
    app: AppInfo,
    isFavourite: Boolean,
    visible: Boolean,
    onDismiss: () -> Unit,
    onToggleFavourite: () -> Unit,
    onHideApp: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.92f),
        exit = fadeOut() + scaleOut(targetScale = 0.92f),
    ) {
        val colors = LocalClearTVColors.current

        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.textPrimary.copy(alpha = 0.3f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            // Menu card
            Column(
                modifier = Modifier
                    .shadow(24.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.background)
                    .border(1.dp, colors.surfaceBorder, RoundedCornerShape(20.dp))
                    .padding(24.dp)
                    .clickable { /* consume click to prevent dismissal */ },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // App name header
                Text(
                    text = app.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Pin / Unpin
                    ContextMenuButton(
                        icon = if (isFavourite) "★" else "☆",
                        label = if (isFavourite) "Unpin" else "Pin",
                        onClick = {
                            onToggleFavourite()
                            onDismiss()
                        },
                    )

                    // Hide
                    ContextMenuButton(
                        icon = "👁",
                        label = "Hide",
                        onClick = {
                            onHideApp()
                            onDismiss()
                        },
                    )

                    // Cancel
                    ContextMenuButton(
                        icon = "✕",
                        label = "Cancel",
                        onClick = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextMenuButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
) {
    val colors = LocalClearTVColors.current

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.surfaceBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary,
        )
    }
}
