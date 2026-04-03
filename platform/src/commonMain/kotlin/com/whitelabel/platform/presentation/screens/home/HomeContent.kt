package com.whitelabel.platform.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whitelabel.core.domain.model.DisplayableItem
import com.whitelabel.core.domain.model.ItemGroup
import com.whitelabel.core.presentation.home.HomeUiState
import com.whitelabel.core.presentation.home.ViewMode
import com.whitelabel.platform.presentation.components.CompactSiteCard
import com.whitelabel.platform.presentation.components.GenericSiteCard
import com.whitelabel.platform.presentation.components.MapView
import com.whitelabel.platform.utils.ExtractedColors

/**
 * Generic home content screen that supports both Grid and Map view modes.
 * Handles loading, empty, error, and success states.
 *
 * @param uiState Current UI state (Loading, Empty, Error, Success)
 * @param viewMode Current view mode (Grid or Map)
 * @param languageCode Current language for localization
 * @param searchQuery Current search query (for empty state messaging)
 * @param focusedItemId ID of item to focus on map (optional)
 * @param onItemClick Callback when an item is clicked
 * @param onFavoriteClick Callback when favorite is toggled
 * @param onClearFocusedItem Callback to clear focused item
 * @param modifier Modifier for the content
 * @param gridColumns Number of columns in grid view (default 2)
 * @param listHeader Optional composable for list header
 * @param groupHeader Optional composable for group headers
 * @param drawableResourceIdProvider Optional function to provide Android drawable resource ID for items (prioritized over URLs)
 * @param colorExtractor Optional function to extract colors from items for dynamic card coloring (Android only)
 */
@Composable
fun <T : DisplayableItem> HomeContent(
    uiState: HomeUiState,
    viewMode: ViewMode,
    languageCode: String,
    searchQuery: String = "",
    focusedItemId: Long? = null,
    onItemClick: (Long) -> Unit,
    onFavoriteClick: (T) -> Unit,
    onClearFocusedItem: () -> Unit = {},
    modifier: Modifier = Modifier,
    gridColumns: Int = 2,
    listHeader: @Composable (() -> Unit)? = null,
    groupHeader: @Composable ((ItemGroup<T>) -> Unit)? = null,
    drawableResourceIdProvider: @Composable ((T) -> Int?)? = null,
    colorExtractor: @Composable ((T) -> ExtractedColors?)? = null
) {
    when (uiState) {
        is HomeUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "No items found" else "No results for \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        is HomeUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is HomeUiState.Success<*> -> {
            @Suppress("UNCHECKED_CAST")
            val successState = uiState as HomeUiState.Success<T>
            
            when (viewMode) {
                ViewMode.Map -> {
                    MapView(
                        items = successState.items,
                        focusedItemId = focusedItemId,
                        onItemClick = onItemClick,
                        onClearFocusedItem = onClearFocusedItem,
                        modifier = modifier.fillMaxSize()
                    )
                }
                ViewMode.Grid -> {
                    GridView(
                        items = successState.items,
                        groups = successState.groups,
                        languageCode = languageCode,
                        onItemClick = onItemClick,
                        onFavoriteClick = onFavoriteClick,
                        modifier = modifier.fillMaxSize(),
                        gridColumns = gridColumns,
                        listHeader = listHeader,
                        groupHeader = groupHeader,
                        drawableResourceIdProvider = drawableResourceIdProvider,
                        colorExtractor = colorExtractor
                    )
                }
            }
        }
    }
}

/**
 * Grid view with grouping support for catalog items.
 */
@Composable
private fun <T : DisplayableItem> GridView(
    items: List<T>,
    groups: List<ItemGroup<T>>,
    languageCode: String,
    onItemClick: (Long) -> Unit,
    onFavoriteClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    gridColumns: Int = 2,
    listHeader: @Composable (() -> Unit)? = null,
    groupHeader: @Composable ((ItemGroup<T>) -> Unit)? = null,
    drawableResourceIdProvider: @Composable ((T) -> Int?)? = null,
    colorExtractor: @Composable ((T) -> ExtractedColors?)? = null
) {
    val gridState = rememberLazyGridState()
    var initialLoadComplete by remember { mutableStateOf(false) }

    LaunchedEffect(items) {
        // Simulate initial load delay for smooth transitions
        kotlinx.coroutines.delay(100)
        initialLoadComplete = true
    }

    if (!initialLoadComplete) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            state = gridState,
            modifier = modifier,
            columns = GridCells.Fixed(gridColumns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // List header
            listHeader?.let { header ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    header()
                }
            }

            // Group headers and items
            groups.forEach { group ->
                // Group header
                item(span = { GridItemSpan(maxLineSpan) }) {
                    groupHeader?.invoke(group) ?: DefaultGroupHeader(group)
                }

                // Items in this group
                items(
                    items = group.items,
                    key = { "${it.id}_${group.key}" }
                ) { item ->
                    val onFavorite = remember(item) {
                        { onFavoriteClick(item) }
                    }
                    val drawableId = drawableResourceIdProvider?.invoke(item)
                    val extractedColors = colorExtractor?.invoke(item)
                    CompactSiteCard(
                        item = item,
                        languageCode = languageCode,
                        onClick = { onItemClick(item.id) },
                        onFavoriteClick = onFavorite,
                        drawableResourceId = drawableId,
                        extractedColors = extractedColors
                    )
                }
            }
        }
    }
}

@Composable
private fun <T : DisplayableItem> DefaultGroupHeader(group: ItemGroup<T>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${group.displayName} (${group.items.size})",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
