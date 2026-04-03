package com.whitelabel.platform.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.whitelabel.core.domain.model.DisplayableItem
import com.whitelabel.platform.utils.ExtractedColors

/**
 * Generic card component for displaying any DisplayableItem.
 * Supports images, favorites, localization, and customizable styling.
 * Prioritizes drawableResourceId over imageUrl if provided.
 * Supports extracted colors from Palette API for dynamic card coloring.
 *
 * @param item The item to display
 * @param languageCode Current language code for localization
 * @param onClick Callback when the card is clicked
 * @param onFavoriteClick Callback when the favorite button is clicked
 * @param modifier Modifier for the card
 * @param imageUrl Optional image URL override (uses item.imageUrls.first() by default)
 * @param drawableResourceId Optional Android drawable resource ID (takes priority over URL)
 * @param extractedColors Optional extracted colors from Palette API for dynamic coloring
 * @param imageHeight Height of the image area
 * @param cardColors Card color scheme (overridden by extractedColors if provided)
 * @param showFavorite Whether to show the favorite button
 * @param titleColor Color for the title text (overridden by extractedColors if provided)
 */
@Composable
fun <T : DisplayableItem> GenericSiteCard(
    item: T,
    languageCode: String,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    drawableResourceId: Int? = null,
    extractedColors: ExtractedColors? = null,
    imageHeight: Dp = 120.dp,
    cardColors: CardColors = CardDefaults.cardColors(),
    showFavorite: Boolean = true,
    titleColor: Color = Color.Unspecified
) {
    val effectiveTitleColor = if (extractedColors != null) {
        val baseColor = extractedColors.getTitleColor()
        baseColor.copy(
            red = baseColor.red * 0.65f,
            green = baseColor.green * 0.65f,
            blue = baseColor.blue * 0.65f
        )
    } else if (titleColor != Color.Unspecified) {
        titleColor.copy(
            red = titleColor.red * 0.65f,
            green = titleColor.green * 0.65f,
            blue = titleColor.blue * 0.65f
        )
    } else {
        LocalContentColor.current.copy(
            red = LocalContentColor.current.red * 0.65f,
            green = LocalContentColor.current.green * 0.65f,
            blue = LocalContentColor.current.blue * 0.65f
        )
    }

    val effectiveCardColors = if (extractedColors != null) {
        CardDefaults.cardColors(
            containerColor = extractedColors.getCardBackgroundColor()
        )
    } else {
        cardColors
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = effectiveCardColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Image - prioritize drawableResourceId, then imageUrl, then item.imageUrls
            val imageToLoad = when {
                drawableResourceId != null && drawableResourceId != 0 -> drawableResourceId
                imageUrl != null -> imageUrl
                else -> item.imageUrls.firstOrNull()
            }
            if (imageToLoad != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val context = LocalPlatformContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageToLoad)
                            .build(),
                        contentDescription = item.getLocalizedName(languageCode),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Content with favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.getLocalizedName(languageCode),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = effectiveTitleColor
                    )

                    item.getLocalizedCategory(languageCode)?.let { category ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodySmall,
                            color = effectiveTitleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (showFavorite) {
                    // Box with pointerInput to prevent click propagation to parent Card
                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures { onFavoriteClick() }
                            }
                    ) {
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (item.isFavorite) Color.Red else LocalContentColor.current,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simplified card variant for grid layouts with fixed dimensions.
 * Supports drawable resource IDs for local images and extracted colors.
 */
@Composable
fun <T : DisplayableItem> CompactSiteCard(
    item: T,
    languageCode: String,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    drawableResourceId: Int? = null,
    extractedColors: ExtractedColors? = null
) {
    GenericSiteCard(
        item = item,
        languageCode = languageCode,
        onClick = onClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier.height(190.dp),
        imageUrl = imageUrl,
        drawableResourceId = drawableResourceId,
        extractedColors = extractedColors,
        imageHeight = 120.dp
    )
}
