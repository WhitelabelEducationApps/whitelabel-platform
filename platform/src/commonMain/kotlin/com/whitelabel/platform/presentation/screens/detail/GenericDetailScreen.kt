package com.whitelabel.platform.presentation.screens.detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.whitelabel.core.domain.model.DisplayableItem
import kotlinx.coroutines.delay

/**
 * Generic detail screen for any DisplayableItem.
 * Provides customizable top bar, content, and floating action buttons.
 *
 * @param viewModel ViewModel providing item details
 * @param onBackClick Callback when back button is pressed
 * @param languageCode Current language code for localization
 * @param modifier Modifier for the screen
 * @param title Custom title composable (defaults to item name)
 * @param topBarColor Custom top bar color (defaults to surface)
 * @param topBarContentColor Color for top bar content
 * @param floatingActionButton Custom FAB composable (optional)
 * @param content Custom content composable (defaults to standard layout). Receives (item, groupName, paddingValues).
 * @param headerImageHeight Height of the header image (default 200.dp)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : DisplayableItem> GenericDetailScreen(
    viewModel: com.whitelabel.core.presentation.detail.ItemDetailViewModel<T>,
    onBackClick: () -> Unit,
    languageCode: String,
    modifier: Modifier = Modifier,
    title: @Composable ((T) -> String)? = null,
    topBarColor: Color = MaterialTheme.colorScheme.surface,
    topBarContentColor: Color = MaterialTheme.colorScheme.onSurface,
    floatingActionButton: @Composable ((T) -> Unit)? = null,
    content: @Composable ((T, String?, PaddingValues) -> Unit)? = null,
    headerImageHeight: androidx.compose.ui.unit.Dp = 200.dp
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    when (uiState) {
                        is com.whitelabel.core.presentation.detail.ItemDetailUiState.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            val item = (uiState as com.whitelabel.core.presentation.detail.ItemDetailUiState.Success<T>).item
                            val titleText = title?.invoke(item) ?: item.getLocalizedName(languageCode)
                            val scrollState = rememberScrollState()
                            
                            LaunchedEffect(titleText) {
                                while (true) {
                                    delay(1500)
                                    scrollState.animateScrollTo(scrollState.maxValue, androidx.compose.animation.core.tween(8000))
                                    delay(1000)
                                    scrollState.animateScrollTo(0, androidx.compose.animation.core.tween(8000))
                                }
                            }
                            
                            Text(
                                text = titleText,
                                maxLines = 1,
                                modifier = Modifier.horizontalScroll(scrollState, enabled = false)
                            )
                        }
                        else -> Text("Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor,
                    titleContentColor = topBarContentColor,
                    navigationIconContentColor = topBarContentColor
                )
            )
        },
        floatingActionButton = {
            if (uiState is com.whitelabel.core.presentation.detail.ItemDetailUiState.Success<*> && floatingActionButton != null) {
                @Suppress("UNCHECKED_CAST")
                val item = (uiState as com.whitelabel.core.presentation.detail.ItemDetailUiState.Success<T>).item
                floatingActionButton(item)
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is com.whitelabel.core.presentation.detail.ItemDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is com.whitelabel.core.presentation.detail.ItemDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = (uiState as com.whitelabel.core.presentation.detail.ItemDetailUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is com.whitelabel.core.presentation.detail.ItemDetailUiState.Success<*> -> {
                @Suppress("UNCHECKED_CAST")
                val successState = uiState as com.whitelabel.core.presentation.detail.ItemDetailUiState.Success<T>
                val item = successState.item
                val groupName = successState.localizedGroupName

                if (content != null) {
                    content(item, groupName, paddingValues)
                } else {
                    DefaultDetailContent(
                        item = item,
                        groupName = groupName,
                        languageCode = languageCode,
                        headerImageHeight = headerImageHeight,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
private fun <T : DisplayableItem> DefaultDetailContent(
    item: T,
    groupName: String?,
    languageCode: String,
    headerImageHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        val context = LocalPlatformContext.current
        val imageUrl = item.imageUrls.firstOrNull()

        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(imageUrl).build(),
                contentDescription = item.getLocalizedName(languageCode),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerImageHeight)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Location info (if coordinates available)
        if (item.latitude != null && item.longitude != null) {
            Text(
                text = "${item.latitude}, ${item.longitude}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Group/category info
        if (!groupName.isNullOrBlank()) {
            Text(
                text = groupName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Description
        val description = item.getLocalizedDescription(languageCode)
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = "No description available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
