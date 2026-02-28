package com.cleartv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cleartv.data.model.AppInfo
import com.cleartv.ui.theme.ClearTVTypography
import com.cleartv.ui.theme.LocalClearTVColors

/**
 * Apps grid — displays all installed non-system apps in a 6-column grid
 * of 1:1 square tiles. The Settings tile is always rendered last.
 */
@Composable
fun AppsGrid(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalClearTVColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Section header
        Text(
            text = "APPS",
            style = ClearTVTypography.sectionHeader,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            // App tiles
            items(
                items = apps,
                key = { it.packageName },
            ) { app ->
                AppTile(
                    app = app,
                    isLarge = false,
                    onClick = { onAppClick(app) },
                )
            }

            // Settings tile — always last
            item(key = "settings") {
                SettingsTile(onClick = onSettingsClick)
            }
        }
    }
}
