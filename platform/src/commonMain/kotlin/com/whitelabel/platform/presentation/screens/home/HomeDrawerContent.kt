package com.whitelabel.platform.presentation.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whitelabel.core.AppConfig
import com.whitelabel.core.presentation.home.ViewMode
import com.whitelabel.core.presentation.home.getAvailableViewModes
import com.whitelabel.platform.utils.*

private const val TAG = "HomeDrawerContent"

@Composable
fun HomeDrawerContent(
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    onLanguageClick: () -> Unit,
    onCloseDrawer: () -> Unit,
    appConfig: AppConfig,
    useLocationFilter: Boolean = false,
    onLocationFilterToggle: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    logLifecycle(TAG, "Composable entered, currentViewMode=$viewMode")
    
    // Use the expect function for localized strings
    val gridViewLabel = getStringResource("grid_view")
    val mapViewLabel = getStringResource("map_view")
    val languageLabel = getStringResource("language")
    val viewOptionsLabel = getStringResource("view_options")
    val useLocationPlantsLabel = getStringResource("location_relevant_items_only")

    val menuItems = mutableListOf<DrawerMenuItem>()

    val availableViewModes = appConfig.getAvailableViewModes()
    val showViewModeSwitcher = availableViewModes.size > 1

    // Only show view mode options if multiple modes are available
    if (showViewModeSwitcher) {
        menuItems.add(DrawerMenuItem(
            label = gridViewLabel,
            icon = null, 
            viewMode = ViewMode.Grid
        ))

        if (appConfig.enableMap) {
            menuItems.add(DrawerMenuItem(
                label = mapViewLabel,
                icon = null,
                viewMode = ViewMode.Map
            ))
        }
    }

    // Always show language option
    menuItems.add(DrawerMenuItem(
        label = languageLabel,
        icon = null,
        isAction = true,
        onClick = onLanguageClick
    ))

    // Location filter toggle
    if (appConfig.enableLocationFilter) {
        menuItems.add(
            DrawerMenuItem(
            label = useLocationPlantsLabel,
            icon = null,
            isAction = true,
            isToggle = true,
            toggleChecked = useLocationFilter,
            onClick = { onLocationFilterToggle(!useLocationFilter) }
        ))
    }

    CatalogNavigationDrawer(
        currentViewMode = viewMode,
        onViewModeChange = { newMode ->
            logUserAction(TAG, "selected view mode", "newMode=$newMode")
            onViewModeChange(newMode)
        },
        menuItems = menuItems,
        headerTitle = viewOptionsLabel,
        onCloseDrawer = {
            debugLogD(TAG, "Closing drawer")
            onCloseDrawer()
        },
        modifier = modifier,
        itemContent = { item, isSelected, onClick ->
            debugLogD(TAG, "Rendering drawer item: ${item.label}, selected=$isSelected, isToggle=${item.isToggle}")
            
            NavigationDrawerItem(
                label = { Text(item.label) },
                icon = null,
                selected = isSelected,
                badge = if (item.isToggle) {
                    {
                        Switch(
                            checked = item.toggleChecked,
                            onCheckedChange = {
                                logUserAction(TAG, "toggled drawer item", item.label)
                                onClick()
                            }
                        )
                    }
                } else null,
                onClick = {
                    logUserAction(TAG, "clicked drawer item", item.label)
                    onClick()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    )
}
