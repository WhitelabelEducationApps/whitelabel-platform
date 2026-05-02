package com.whitelabel.platform.presentation.screens.home

import androidx.compose.runtime.Composable
import com.whitelabel.platform.utils.getStringResource
import com.whitelabel.core.presentation.components.SearchTopAppBar

@Composable
fun HomeTopAppBar(
    searchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onOpenDrawer: () -> Unit,
) {
    SearchTopAppBar(
        appTitle = getStringResource("app_name"),
        searchActive = searchActive,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        onSearchActiveChange = onSearchActiveChange,
        onOpenDrawer = onOpenDrawer,
        searchPlaceholder = getStringResource("search_placeholder"),
        closeSearchDescription = getStringResource("close_search"),
        clearSearchDescription = getStringResource("clear_search"),
        menuDescription = getStringResource("menu"),
        searchDescription = getStringResource("search"),
    )
}
