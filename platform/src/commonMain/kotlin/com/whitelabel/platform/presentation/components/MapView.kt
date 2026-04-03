package com.whitelabel.platform.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.whitelabel.core.domain.model.DisplayableItem

/**
 * Expect composable for platform-specific map view.
 * Implementations provide Google Maps (Android) or MapKit (iOS) integration.
 *
 * @param items List of items with location data to display on the map
 * @param focusedItemId ID of item to focus/center on (optional)
 * @param onItemClick Callback when a map marker is clicked
 * @param onClearFocusedItem Callback to clear the focused item after navigation
 * @param modifier Modifier for the map component
 */
@Composable
expect fun <T : DisplayableItem> MapView(
    items: List<T>,
    focusedItemId: Long? = null,
    onItemClick: (Long) -> Unit,
    onClearFocusedItem: () -> Unit = {},
    modifier: Modifier = Modifier
)
