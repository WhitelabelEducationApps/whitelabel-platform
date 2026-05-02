package com.whitelabel.platform.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.whitelabel.platform.utils.LocationFilterPreferences
import com.whitelabel.platform.utils.OnboardingPreferences
import com.whitelabel.platform.utils.getContext
import com.whitelabel.platform.utils.getLocationLastKnown
import com.whitelabel.platform.utils.getStringResource
import com.whitelabel.platform.utils.hasLocationPermission
import com.whitelabel.platform.utils.rememberLocationPermissionLauncher
import com.whitelabel.core.AppConfig
import com.whitelabel.core.presentation.home.HomeUiState
import com.whitelabel.core.presentation.home.HomeViewModel
import com.whitelabel.core.presentation.home.ViewMode
import com.whitelabel.platform.data.models.CatalogItem
import com.whitelabel.platform.utils.ZoneGeoMapper
import com.whitelabel.platform.utils.debugLogD
import com.whitelabel.platform.utils.logLifecycle
import com.whitelabel.platform.utils.logStateChange
import com.whitelabel.platform.utils.logUserAction
import com.whitelabel.platform.utils.LanguagePreferences
import com.whitelabel.platform.components.getHomeBackgroundPainter
import com.whitelabel.platform.components.getSiteDrawableId
import com.whitelabel.platform.components.getSiteDrawableIds
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel<CatalogItem>,
    appConfig: AppConfig,
    onSiteClick: (Long) -> Unit,
    onNavigateToLanguage: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    logLifecycle(TAG, "Composable entered")

    val context = getContext()
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val focusedSiteId by viewModel.focusedItemId.collectAsState()
    val selectedLanguage by LanguagePreferences.selectedLanguage.collectAsState()
    val languageCode = selectedLanguage?.code ?: "en"
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchActive by rememberSaveable { mutableStateOf(false) }

    val useLocationFilter by LocationFilterPreferences.useLocationFilter.collectAsState()
    val currentUserZones by LocationFilterPreferences.currentUserZones.collectAsState()
    val totalItemCount by viewModel.totalItemCount.collectAsState()
    val onboardingShown by OnboardingPreferences.onboardingShown.collectAsState()
    val homeBackgroundPainter = getHomeBackgroundPainter()
    var showOnboardingDialog by remember { mutableStateOf(false) }

    // Pre-collect @Composable strings so they can be used safely in callbacks
    val locationDeniedMessage = getStringResource("location_permission_denied")
    val onboardingTitle = getStringResource("onboarding_title")
    val onboardingMessage = getStringResource("onboarding_message")
    val onboardingOk = getStringResource("onboarding_ok")

    LaunchedEffect(onboardingShown) {
        if (!onboardingShown) {
            showOnboardingDialog = true
        }
    }

    val permissionLauncher = rememberLocationPermissionLauncher(
        onGranted = {
            scope.launch {
                val location = getLocationLastKnown(context)
                if (location != null) {
                    val zones = ZoneGeoMapper.getZonesForLocation(location.first, location.second)
                    LocationFilterPreferences.setCurrentUserZones(zones)
                    LocationFilterPreferences.setUseLocationFilter(zones.isNotEmpty())
                    debugLogD(TAG, "Location granted, zones=$zones")
                } else {
                    debugLogD(TAG, "Location granted but no fix yet — filter not enabled")
                    LocationFilterPreferences.setUseLocationFilter(false)
                }
            }
        }
    ) {
        LocationFilterPreferences.setUseLocationFilter(false)
        scope.launch { snackbarHostState.showSnackbar(locationDeniedMessage) }
        debugLogD(TAG, "Location permission denied, toggle reverted")
    }

    DisposableEffect(uiState) {
        debugLogD(TAG, "uiState updated")
        onDispose { }
    }

    DisposableEffect(viewMode) {
        logStateChange(TAG, "viewMode", null, viewMode)
        onDispose { }
    }

    DisposableEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) debugLogD(TAG, "searchQuery: '$searchQuery'")
        onDispose { }
    }

    DisposableEffect(focusedSiteId) {
        if (focusedSiteId != null) debugLogD(TAG, "focusedSiteId: $focusedSiteId")
        onDispose { }
    }

    if (showOnboardingDialog) {
        AlertDialog(
            onDismissRequest = {
                showOnboardingDialog = false
                OnboardingPreferences.markShown()
            },
            title = { Text(onboardingTitle) },
            text = { Text(onboardingMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showOnboardingDialog = false
                    OnboardingPreferences.markShown()
                }) {
                    Text(onboardingOk)
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        homeBackgroundPainter?.let { painter ->
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.18f,
                modifier = Modifier.fillMaxSize()
            )
        }
        ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = viewMode != ViewMode.Map,
        drawerContent = {
            logLifecycle(TAG, "Drawer content composed")
            HomeDrawerContent(
                viewMode = viewMode,
                onViewModeChange = { newMode ->
                    logUserAction(TAG, "switched view mode", "from $viewMode to $newMode")
                    viewModel.setViewMode(newMode)
                },
                onLanguageClick = {
                    logUserAction(TAG, "opened language selection")
                    onNavigateToLanguage()
                },
                appConfig = appConfig,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onLocationFilterToggle = { newValue ->
                    logUserAction(TAG, "toggled location filter", "newValue=$newValue")
                    if (newValue) {
                        if (hasLocationPermission(context)) {
                            scope.launch {
                                val location = getLocationLastKnown(context)
                                val zones = location?.let {
                                    ZoneGeoMapper.getZonesForLocation(it.first, it.second)
                                } ?: emptyList()
                                LocationFilterPreferences.setCurrentUserZones(zones)
                                LocationFilterPreferences.setUseLocationFilter(zones.isNotEmpty())
                                debugLogD(TAG, "Location already granted, zones=$zones")
                            }
                        } else {
                            permissionLauncher()
                        }
                    } else {
                        LocationFilterPreferences.setUseLocationFilter(false)
                    }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = if (homeBackgroundPainter != null) Color.Transparent else MaterialTheme.colorScheme.background,
            topBar = {
                HomeTopAppBar(
                    searchActive = searchActive,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { query ->
                        if (query != searchQuery) debugLogD(TAG, "search query changed: '$query'")
                        viewModel.onSearchQueryChange(query)
                    },
                    onSearchActiveChange = { active ->
                        logStateChange(TAG, "searchActive", searchActive, active)
                        searchActive = active
                    },
                    onOpenDrawer = {
                        logUserAction(TAG, "opened navigation drawer")
                        scope.launch { drawerState.open() }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
                val filteredCount = (uiState as? HomeUiState.Success<*>)?.items?.size ?: 0
                HomeContent<CatalogItem>(
                    uiState = uiState,
                    viewMode = viewMode,
                    searchQuery = searchQuery,
                    onItemClick = { siteId ->
                        logUserAction(TAG, "clicked site", "siteId=$siteId")
                        onSiteClick(siteId)
                    },
                    onFavoriteClick = { site ->
                        logUserAction(TAG, "toggled favorite", "siteId=${site.id}, name=${site.name}")
                        viewModel.onFavoriteClick(site)
                    },
                    onClearFocusedItem = { viewModel.clearFocusedItem() },
                    drawableResourceIdProvider = { site -> getSiteDrawableId(site) },
                    drawableResourceIdsProvider = { site -> getSiteDrawableIds(site) },
                    colorExtractor = { site ->
                        val drawableId = getSiteDrawableId(site)
                        rememberExtractedColors(site.id, drawableId)
                    },
                    showCategory = appConfig.enableCategories,
                    languageCode = languageCode,
                    listHeader = if (appConfig.enableLocationFilter && useLocationFilter && currentUserZones.isNotEmpty()) {
                        { LocationFilterBanner(filteredCount = filteredCount, totalCount = totalItemCount) }
                    } else null
                )
            }
        }
        }
    }
}
