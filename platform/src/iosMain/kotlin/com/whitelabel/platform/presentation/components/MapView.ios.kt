package com.whitelabel.platform.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.whitelabel.core.domain.model.DisplayableItem
import com.whitelabel.platform.utils.ExtractedColors

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
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Map View (iOS Stub)")
    }
}
