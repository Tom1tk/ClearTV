package com.cleartv.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.ui.widgets.ClockWidget
import com.cleartv.ui.widgets.StatusWidget
import com.cleartv.ui.widgets.WeatherWidget
import com.cleartv.util.IntentUtil

/**
 * Root composable for the ClearTV home screen.
 *
 * Uses a single LazyColumn as the root scroll container.
 * AppsGrid uses FlowRow (non-lazy) to avoid the Compose
 * "nested lazy layout" crash (StackOverflow in measure pass).
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {},
) {
    val colors = LocalClearTVColors.current
    val context = LocalContext.current

    val visibleApps by viewModel.visibleApps.collectAsState()
    val favourites by viewModel.favourites.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val contextMenuApp by viewModel.contextMenuApp.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val weatherLocationName by viewModel.weatherLocationName.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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
            // Decorative blobs
            Box(
                modifier = Modifier
                    .size(480.dp)
                    .offset(x = 200.dp, y = (-120).dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(colors.blobBlue, Color.Transparent)
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
                                listOf(colors.blobGreen, Color.Transparent)
                            ),
                            radius = size.minDimension / 2,
                        )
                    }
            )

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.textSecondary)
                }
            } else {
                val gridApps = visibleApps.filter { app ->
                    favourites.none { fav -> fav.packageName == app.packageName }
                }

                // Single LazyColumn owns all scrolling — avoids nested scroll crash
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 52.dp),
                    contentPadding = PaddingValues(vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    // ── Top bar: Weather + Clock + Status ──
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            if (preferences.showWeather) {
                                WeatherWidget(
                                    weather = weather,
                                    locationName = weatherLocationName,
                                    useCelsius = preferences.weatherCelsius,
                                )
                            } else {
                                Spacer(Modifier.weight(1f))
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                if (preferences.showClock) {
                                    ClockWidget()
                                    Spacer(Modifier.height(12.dp))
                                }
                                StatusWidget(
                                    onClick = { context.startActivity(IntentUtil.systemSettings()) },
                                )
                            }
                        }
                    }

                    // ── Favourites row ──
                    item {
                        FavouritesSection(
                            favourites = favourites,
                            onAppClick = { launchApp(context, viewModel, it) },
                            onAppLongClick = { viewModel.showContextMenu(it) },
                        )
                    }

                    // ── Apps grid header ──
                    item {
                        Text(
                            text = "APPS",
                            style = ClearTVTypography.sectionHeader,
                            color = colors.textSecondary,
                        )
                    }

                    // ── Apps grid — FlowRow in chunks for non-lazy grid ──
                    item {
                        AppsFlowGrid(
                            apps = gridApps,
                            onAppClick = { launchApp(context, viewModel, it) },
                            onAppLongClick = { viewModel.showContextMenu(it) },
                            onSettingsClick = onNavigateToSettings,
                        )
                    }
                }
            }
        }

        // Context menu overlay
        if (contextMenuApp != null) {
            ContextMenu(
                app = contextMenuApp!!,
                isFavourite = preferences.favouritePackages.contains(contextMenuApp!!.packageName),
                visible = true,
                onDismiss = { viewModel.dismissContextMenu() },
                onToggleFavourite = { viewModel.toggleFavourite(contextMenuApp!!.packageName) },
                onHideApp = { viewModel.hideApp(contextMenuApp!!.packageName) },
            )
        }
    }
}

@Composable
private fun FavouritesSection(
    favourites: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
) {
    val colors = LocalClearTVColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "FAVOURITES",
            style = ClearTVTypography.sectionHeader,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        if (favourites.isEmpty()) {
            Text(
                text = "Long-press an app to add it to favourites",
                style = ClearTVTypography.tileLabelSmall,
                color = colors.textSecondary,
                modifier = Modifier.padding(vertical = 24.dp),
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(end = 14.dp),
            ) {
                items(favourites, key = { it.packageName }) { app ->
                    AppTile(
                        app = app,
                        isLarge = true,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) },
                        modifier = Modifier.fillParentMaxWidth(
                            1f / minOf(favourites.size, 4).coerceAtLeast(1) - 0.02f
                        ),
                    )
                }
            }
        }
    }
}

/**
 * Non-lazy 6-column grid using FlowRow.
 * Avoids the Compose crash caused by LazyVerticalGrid inside LazyColumn.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppsFlowGrid(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onSettingsClick: () -> Unit,
    columns: Int = 6,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = columns,
    ) {
        apps.forEach { app ->
            AppTile(
                app = app,
                isLarge = false,
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongClick(app) },
                modifier = Modifier.weight(1f),
            )
        }
        // Fill remainder of last row with empty weight slots + always-last settings tile
        SettingsTile(
            onClick = onSettingsClick,
            modifier = Modifier.weight(1f),
        )
        // Pad remaining cells in last row so tiles don't stretch
        val totalItems = apps.size + 1 // +1 for settings
        val remainder = if (totalItems % columns == 0) 0 else columns - (totalItems % columns)
        repeat(remainder) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private fun launchApp(context: Context, viewModel: HomeViewModel, app: AppInfo) {
    val intent = viewModel.getLaunchIntent(app)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
