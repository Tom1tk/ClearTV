package com.cleartv.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.ui.util.outerFocusRing
import com.cleartv.ui.util.tileFocusedShadow
import com.cleartv.ui.util.tileRestingShadow
import androidx.compose.ui.graphics.Color

/**
 * Reusable app tile composable for both the Favourites row (16:9) and
 * the Apps grid (1:1). Implements the frosted glass card aesthetic,
 * outer focus ring, scale animation with spring bounce, and label bar
 * overlay from the spec.
 *
 * Supports long-press for context menu.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTile(
    app: AppInfo,
    isLarge: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current
    var isFocused by remember { mutableStateOf(false) }

    // Scale animation: 1.06× on focus with bouncy spring (ζ=0.65 → ~7% overshoot)
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 380f,
        ),
        label = "tileScale",
    )

    // Label bar fade
    val labelAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(150),
        label = "labelAlpha",
    )

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
            // Focus ring + shadow drawn BEFORE clip so they render outside the clipped area
            .then(
                if (isFocused) Modifier.outerFocusRing(colors.focusRing, cornerRadius = cornerRadius)
                else Modifier
            )
            .then(
                if (isFocused) Modifier.tileFocusedShadow(cornerRadius)
                else Modifier.tileRestingShadow(cornerRadius)
            )
            .clip(shape)
            .background(colors.surface)
            .onFocusChanged { isFocused = it.isFocused }
            .semantics {
                contentDescription = "${app.label}. ${if (isLarge) "Favourite app" else "App"}. Press to open. Long press for options."
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // App icon — centered in tile
        AsyncImage(
            model = app.icon,
            contentDescription = app.label,
            modifier = Modifier
                .size(if (isLarge) 64.dp else 48.dp)
                .clip(RoundedCornerShape(if (isLarge) 16.dp else 12.dp)),
            contentScale = ContentScale.Fit,
        )

        // Label bar — fades in at bottom on focus
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .graphicsLayer { alpha = labelAlpha }
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

/**
 * Settings tile — always appears last in the grid.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 380f),
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
            .then(
                if (isFocused) Modifier.outerFocusRing(colors.focusRing, cornerRadius = 16.dp)
                else Modifier
            )
            .then(
                if (isFocused) Modifier.tileFocusedShadow(16.dp)
                else Modifier.tileRestingShadow(16.dp)
            )
            .clip(shape)
            .background(colors.settingsTileBg)
            .onFocusChanged { isFocused = it.isFocused }
            .combinedClickable(onClick = onClick),
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
