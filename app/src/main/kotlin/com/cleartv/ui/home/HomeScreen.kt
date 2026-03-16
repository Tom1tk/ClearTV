package com.cleartv.ui.home

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.ui.widgets.ClockWidget
import com.cleartv.ui.widgets.StatusWidget
import com.cleartv.ui.widgets.WeatherWidget
import com.cleartv.util.IntentUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Root composable for the ClearTV home screen.
 *
 * Single LazyColumn owns all scrolling (avoids nested lazy crash).
 * FlowRow is used for the apps grid (non-lazy, safe inside LazyColumn).
 *
 * TV scroll behaviour: when D-pad focus returns to the Favourites section
 * (the topmost interactive zone), the list automatically animates back to
 * item 0, keeping weather and clock fully visible. Favourites are the
 * highest focusable elements on the screen.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {},
) {
    val colors = LocalClearTVColors.current
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val visibleApps by viewModel.visibleApps.collectAsState()
    val favourites by viewModel.favourites.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val contextMenuApp by viewModel.contextMenuApp.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val weatherLocationName by viewModel.weatherLocationName.collectAsState()
    val launchingApp by viewModel.launchingApp.collectAsState()

    // Dismiss launch overlay on Back press
    BackHandler(enabled = launchingApp != null) {
        viewModel.setLaunchingApp(null)
    }

    // Track screen size for the 160° gradient
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { screenSize = it },
    ) {
        // Background gradient — 160° (near-vertical, slight left→right tilt)
        val gradientStart = if (screenSize.width > 0) {
            Offset(
                screenSize.width * 0.5f - screenSize.width * 0.171f,
                0f,
            )
        } else {
            Offset(0f, 0f)
        }
        val gradientEnd = if (screenSize.width > 0) {
            Offset(
                screenSize.width * 0.5f + screenSize.width * 0.171f,
                screenSize.height.toFloat(),
            )
        } else {
            Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.background, colors.backgroundEnd),
                        start = gradientStart,
                        end = gradientEnd,
                    )
                )
        ) {
            // Blue blob — top-right corner, partially off-screen
            Box(
                modifier = Modifier
                    .size(480.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 60.dp, y = (-120).dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(listOf(colors.blobBlue, Color.Transparent)),
                            radius = size.minDimension / 2,
                        )
                    }
            )
            // Green blob — bottom-left
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 80.dp, y = 80.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(listOf(colors.blobGreen, Color.Transparent)),
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

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 52.dp),
                    contentPadding = PaddingValues(bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    // ── Weather + Clock + Status (non-interactive, no focus) ──
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 36.dp),
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
                                    ClockWidget(is12Hour = preferences.weather12hr)
                                    Spacer(Modifier.height(12.dp))
                                }
                                StatusWidget(
                                    onClick = { context.startActivity(IntentUtil.systemSettings()) },
                                )
                            }
                        }
                    }

                    // ── Favourites (topmost focusable zone) ──
                    // When any favourite tile receives focus, scroll the list back to
                    // the very top so weather and clock are always fully visible.
                    item {
                        FavouritesSection(
                            favourites = favourites,
                            onAppClick = { launchApp(context, viewModel, it, scope) },
                            onAppLongClick = { viewModel.showContextMenu(it) },
                            onFocusEntered = {
                                scope.launch { listState.animateScrollToItem(0) }
                            },
                        )
                    }

                    // ── Apps header ──
                    item {
                        Text(
                            text = "APPS",
                            style = ClearTVTypography.sectionHeader,
                            color = colors.textSecondary,
                        )
                    }

                    // ── Apps grid (FlowRow — non-lazy, safe inside LazyColumn) ──
                    item {
                        AppsFlowGrid(
                            apps = gridApps,
                            onAppClick = { launchApp(context, viewModel, it, scope) },
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

        // Launch overlay
        if (launchingApp != null) {
            LaunchOverlay(
                app = launchingApp!!,
                onDismiss = { viewModel.setLaunchingApp(null) },
            )
        }
    }
}

@Composable
private fun FavouritesSection(
    favourites: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onFocusEntered: () -> Unit,
) {
    val colors = LocalClearTVColors.current

    // onFocusChanged with hasFocus=true fires when this composable OR any
    // of its children gains focus — i.e. whenever a favourites tile is selected.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.hasFocus) onFocusEntered() },
    ) {
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
            // vertical contentPadding gives tiles room to scale + show outer ring
            // without being clipped at the LazyRow viewport boundary
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
            ) {
                items(favourites, key = { it.packageName }) { app ->
                    AppTile(
                        app = app,
                        isLarge = true,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) },
                        modifier = Modifier.width(260.dp),
                    )
                }
            }
        }
    }
}

/**
 * Non-lazy 6-column grid using FlowRow.
 * Safe inside LazyColumn because FlowRow is not a lazy layout.
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
        SettingsTile(
            onClick = onSettingsClick,
            modifier = Modifier.weight(1f),
        )
        val totalItems = apps.size + 1
        val remainder = if (totalItems % columns == 0) 0 else columns - (totalItems % columns)
        repeat(remainder) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private fun launchApp(
    context: Context,
    viewModel: HomeViewModel,
    app: AppInfo,
    scope: CoroutineScope,
) {
    scope.launch {
        viewModel.setLaunchingApp(app)
        delay(200)
        val intent = viewModel.getLaunchIntent(app)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        viewModel.setLaunchingApp(null)
    }
}
