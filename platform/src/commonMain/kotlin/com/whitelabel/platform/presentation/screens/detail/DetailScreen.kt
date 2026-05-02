package com.whitelabel.platform.presentation.screens.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.whitelabel.platform.utils.LanguagePreferences
import com.whitelabel.platform.utils.getStringResource
import com.whitelabel.core.presentation.detail.ZoomableImageScreen
import com.whitelabel.core.presentation.detail.ItemDetailViewModel
import com.whitelabel.platform.data.models.CatalogItem

@Composable
fun DetailScreen(
    viewModel: ItemDetailViewModel<CatalogItem>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLanguage by LanguagePreferences.selectedLanguage.collectAsState()
    val languageCode = selectedLanguage?.code ?: "en"

    ZoomableImageScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        title = { it.getLocalizedName(languageCode) },
        imageUrl = { it.imageUrl?.split(",")?.firstOrNull()?.trim() },
        wallpaperIcon = {
            // Placeholder icon for now to ensure compilation
            // In a real scenario, we'd use painterResource(Res.drawable.wallpaper)
        },
        backDescription = getStringResource("back"),
        wallpaperSuccessMessage = getStringResource("wallpaper_success"),
        modifier = modifier
    )
}
