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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.LocalClearTVColors
import com.cleartv.ui.widgets.ClockWidget
import com.cleartv.ui.widgets.StatusWidget
import com.cleartv.ui.widgets.WeatherWidget
import com.cleartv.util.IntentUtil

/**
 * Root composable for the ClearTV home screen.
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
            // Subtle background blobs
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = colors.textSecondary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 52.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    // ── Top bar ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        // Left — Weather widget
                        if (preferences.showWeather) {
                            WeatherWidget(
                                weather = weather,
                                locationName = weatherLocationName,
                                useCelsius = preferences.weatherCelsius,
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        // Right — Clock + Status
                        Column(horizontalAlignment = Alignment.End) {
                            if (preferences.showClock) {
                                ClockWidget()
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            StatusWidget(
                                onClick = {
                                    context.startActivity(IntentUtil.systemSettings())
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Favourites ──
                    FavouritesRow(
                        favourites = favourites,
                        onAppClick = { app -> launchApp(context, viewModel, app) },
                        onAppLongClick = { app -> viewModel.showContextMenu(app) },
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Apps grid ──
                    val gridApps = visibleApps.filter { app ->
                        favourites.none { fav -> fav.packageName == app.packageName }
                    }

                    AppsGrid(
                        apps = gridApps,
                        onAppClick = { app -> launchApp(context, viewModel, app) },
                        onAppLongClick = { app -> viewModel.showContextMenu(app) },
                        onSettingsClick = onNavigateToSettings,
                    )
                }
            }
        }

        // ── Context menu overlay ──
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

private fun launchApp(context: Context, viewModel: HomeViewModel, app: AppInfo) {
    val intent = viewModel.getLaunchIntent(app)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
