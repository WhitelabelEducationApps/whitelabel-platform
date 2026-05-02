package com.whitelabel.platform.presentation.screens.language

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.whitelabel.core.presentation.language.LanguageSelectionViewModel
import com.whitelabel.platform.utils.getStringResource
import com.whitelabel.platform.utils.restartActivity
import com.whitelabel.core.presentation.language.LanguageSelectionScreen as CoreLanguageSelectionScreen

@Composable
fun LanguageSelectionScreen(
    viewModel: LanguageSelectionViewModel,
    onNavigateBack: () -> Unit,
    onLanguageChanged: (LanguageSelectionViewModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    CoreLanguageSelectionScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        onLanguageChanged = { vm ->
            onLanguageChanged(vm)
            // Restart the activity if needed
            restartActivity()
        },
        title = getStringResource("select_language"),
        automaticLabel = getStringResource("automatic"),
        backDescription = getStringResource("back"),
        selectedDescription = getStringResource("selected"),
        modifier = modifier
    )
}
