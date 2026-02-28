package com.cleartv.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size

/**
 * Reusable app tile composable for both the Favourites row (16:9) and
 * the Apps grid (1:1). Implements the frosted glass card aesthetic,
 * focus ring, scale animation, and label overlay from the spec.
 */
@Composable
fun AppTile(
    app: AppInfo,
    isLarge: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current
    var isFocused by remember { mutableStateOf(false) }

    // Scale animation: 1.07× on focus with spring physics
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.07f else 1f,
        animationSpec = spring(
            dampingRatio = 0.55f,    // slightly underdamped for spring feel
            stiffness = 400f,
        ),
        label = "tileScale",
    )

    // Shadow intensity changes on focus
    val elevation = if (isFocused) 12.dp else 2.dp
    val cornerRadius = if (isLarge) 20.dp else 16.dp
    val shape = RoundedCornerShape(cornerRadius)
    val aspectRatio = if (isLarge) 16f / 9f else 1f

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation, shape)
            .clip(shape)
            .background(colors.surface)
            .then(
                if (isFocused) {
                    Modifier.border(3.dp, colors.focusRing, shape)
                } else {
                    Modifier.border(1.dp, colors.surfaceBorder, shape)
                }
            )
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        // App icon — fills the tile
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = app.icon,
                contentDescription = app.label,
                modifier = Modifier
                    .size(if (isLarge) 64.dp else 48.dp)
                    .clip(RoundedCornerShape(if (isLarge) 16.dp else 12.dp)),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Always show label for TV readability
            Text(
                text = app.label,
                style = if (isLarge) ClearTVTypography.tileLabel else ClearTVTypography.tileLabelSmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            )
        }

        // Focus label bar at bottom (visible on focus only)
        if (isFocused) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(colors.labelOverlay)
                    .padding(
                        horizontal = if (isLarge) 10.dp else 6.dp,
                        vertical = if (isLarge) 6.dp else 4.dp,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = app.label,
                    style = if (isLarge) ClearTVTypography.tileLabel else ClearTVTypography.tileLabelSmall,
                    color = colors.labelText,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Settings tile — always appears last in the grid.
 * Uses a gear icon and distinct styling.
 */
@Composable
fun SettingsTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.07f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f),
        label = "settingsScale",
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(if (isFocused) 12.dp else 2.dp, shape)
            .clip(shape)
            .background(colors.settingsTileBg)
            .then(
                if (isFocused) {
                    Modifier.border(3.dp, colors.focusRing, shape)
                } else {
                    Modifier.border(1.dp, colors.surfaceBorder, shape)
                }
            )
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "⚙",
                fontSize = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp),
                color = colors.settingsTileFg,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Settings",
                style = ClearTVTypography.tileLabelSmall,
                color = colors.settingsTileFg,
            )
        }
    }
}
