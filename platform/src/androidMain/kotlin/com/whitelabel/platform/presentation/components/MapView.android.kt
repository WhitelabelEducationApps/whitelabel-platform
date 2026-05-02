package com.whitelabel.platform.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.whitelabel.core.domain.model.DisplayableItem
import com.whitelabel.platform.presentation.screens.map.MapScreen
import com.whitelabel.platform.utils.ExtractedColors

/**
 * Android actual implementation of MapView using Google Maps Compose.
 * Provides clustering, camera animations, and marker interactions.
 */
@Composable
actual fun <T : DisplayableItem> MapView(
    items: List<T>,
    focusedItemId: Long?,
    onItemClick: (Long) -> Unit,
    onClearFocusedItem: () -> Unit,
    modifier: Modifier,
    drawableResourceIdProvider: @Composable ((T) -> Int?)?,
    colorExtractor: @Composable ((T) -> ExtractedColors?)?
) {
    MapScreen(
        items = items,
        focusedItemId = focusedItemId,
        onItemClick = onItemClick,
        onClearFocusedItem = onClearFocusedItem,
        modifier = modifier,
        drawableResourceIdProvider = drawableResourceIdProvider,
        colorExtractor = colorExtractor
    )
}
