package com.whitelabel.platform.presentation.screens.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whitelabel.core.presentation.home.ViewMode

/**
 * Data class representing a drawer menu item.
 *
 * @param label Display label for the menu item
 * @param icon Icon resource or composable identifier
 * @param viewMode Associated view mode (for view switcher items)
 * @param isAction Whether this is an action item (like Language) rather than a view mode
 * @param onClick Callback when the item is clicked
 */
data class DrawerMenuItem(
    val label: String,
    val icon: Any? = null, // Can be ImageVector, Painter, or resource reference
    val viewMode: ViewMode? = null,
    val isAction: Boolean = false,
    val onClick: (() -> Unit)? = null
)

/**
 * Generic navigation drawer for catalog apps.
 * Supports view mode switching and action items.
 *
 * @param currentViewMode Currently selected view mode
 * @param onViewModeChange Callback when view mode changes
 * @param menuItems List of drawer menu items
 * @param headerTitle Optional title for the drawer header
 * @param onCloseDrawer Callback to close the drawer
 * @param modifier Modifier for the drawer sheet
 * @param itemContent Custom composable for rendering menu items (optional)
 */
@Composable
fun CatalogNavigationDrawer(
    currentViewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    menuItems: List<DrawerMenuItem>,
    headerTitle: String = "View Options",
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable ((DrawerMenuItem, Boolean, () -> Unit) -> Unit)? = null
) {
    ModalDrawerSheet(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = headerTitle,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        menuItems.forEachIndexed { index, item ->
            val isSelected = item.viewMode?.let { it == currentViewMode } ?: false
            
            val onItemClick = {
                if (item.isAction) {
                    item.onClick?.invoke()
                } else if (item.viewMode != null) {
                    onViewModeChange(item.viewMode)
                }
                onCloseDrawer()
            }

            if (itemContent != null) {
                itemContent(item, isSelected, onItemClick)
            } else {
                DefaultDrawerItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = onItemClick
                )
            }

            // Add divider between sections (after view mode items, before actions)
            if (index < menuItems.size - 1 && 
                !item.isAction && 
                menuItems.getOrNull(index + 1)?.isAction == true) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DefaultDrawerItem(
    item: DrawerMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(item.label) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        icon = item.icon?.let { icon ->
            {
                when (icon) {
                    is androidx.compose.ui.graphics.vector.ImageVector -> {
                        androidx.compose.material3.Icon(
                            imageVector = icon,
                            contentDescription = item.label
                        )
                    }
                    is androidx.compose.ui.graphics.painter.Painter -> {
                        androidx.compose.material3.Icon(
                            painter = icon,
                            contentDescription = item.label
                        )
                    }
                    else -> {
                        // Fallback for other icon types
                    }
                }
            }
        }
    )
}
