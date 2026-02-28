package com.cleartv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors

/**
 * Favourites row — displays up to 6 user-pinned apps as large 16:9 tiles.
 * Uses a horizontal LazyRow. In Phase 1, favourites are hardcoded;
 * Phase 2 will wire this to DataStore preferences.
 */
@Composable
fun FavouritesRow(
    favourites: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Section header
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
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(end = 14.dp),
            ) {
                items(
                    items = favourites,
                    key = { it.packageName },
                ) { app ->
                    AppTile(
                        app = app,
                        isLarge = true,
                        onClick = { onAppClick(app) },
                        modifier = Modifier.fillParentMaxWidth(
                            1f / minOf(favourites.size, 4).coerceAtLeast(1)
                            - 0.02f  // account for gap
                        ),
                    )
                }
            }
        }
    }
}
