package com.cleartv.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.ui.widgets.ClockWidget
import com.cleartv.util.IntentUtil

/**
 * Root composable for the ClearTV home screen.
 * Assembles the background, top bar (clock + status), favourites row,
 * and the apps grid into the main launcher layout.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
) {
    val colors = LocalClearTVColors.current
    val context = LocalContext.current

    val allApps by viewModel.allApps.collectAsState()
    val favourites by viewModel.favourites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colors.background, colors.backgroundEnd),
                    start = Offset(0f, 0f),
                    end = Offset(Float.MAX_VALUE, Float.MAX_VALUE),
                )
            )
    ) {
        // Subtle background blobs (decorative)
        Box(
            modifier = Modifier
                .size(480.dp)
                .offset(x = 200.dp, y = (-120).dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.blobBlue, Color.Transparent),
                        ),
                        radius = size.minDimension / 2,
                    )
                }
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomStart)
                .offset(x = 80.dp, y = 80.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.blobGreen, Color.Transparent),
                        ),
                        radius = size.minDimension / 2,
                    )
                }
        )

        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = colors.textSecondary)
            }
        } else {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 52.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                // ── Top bar: Clock + Status ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    // Left side — placeholder for weather widget (Phase 3)
                    Spacer(modifier = Modifier.weight(1f))

                    // Right side — Clock + Status
                    Column(horizontalAlignment = Alignment.End) {
                        ClockWidget()

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status strip
                        StatusStrip(
                            onStatusClick = {
                                context.startActivity(IntentUtil.systemSettings())
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── Favourites row ──
                FavouritesRow(
                    favourites = favourites,
                    onAppClick = { app -> launchApp(context, viewModel, app) },
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── Apps grid ──
                // Filter out favourites from the main grid to avoid duplication
                val gridApps = allApps.filter { app ->
                    favourites.none { fav -> fav.packageName == app.packageName }
                }

                AppsGrid(
                    apps = gridApps,
                    onAppClick = { app -> launchApp(context, viewModel, app) },
                    onSettingsClick = {
                        context.startActivity(IntentUtil.systemSettings())
                    },
                )
            }
        }
    }
}

/**
 * Status strip — WiFi signal + device name.
 * Navigates to system settings on click/select.
 */
@Composable
private fun StatusStrip(
    onStatusClick: () -> Unit,
) {
    val colors = LocalClearTVColors.current

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .background(
                color = colors.statusSurface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 14.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "WiFi ▸ Strong",
            style = com.cleartv.ui.theme.ClearTVTypography.status,
            color = colors.statusText,
        )
        Text(
            text = "|",
            style = com.cleartv.ui.theme.ClearTVTypography.status,
            color = colors.statusText.copy(alpha = 0.3f),
        )
        Text(
            text = android.os.Build.MODEL,
            style = com.cleartv.ui.theme.ClearTVTypography.status,
            color = colors.statusText,
        )
    }
}

/**
 * Launch an app by its package name.
 */
private fun launchApp(context: Context, viewModel: HomeViewModel, app: AppInfo) {
    val intent = viewModel.getLaunchIntent(app)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
