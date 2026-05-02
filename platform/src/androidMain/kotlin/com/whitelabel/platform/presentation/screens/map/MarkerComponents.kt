package com.whitelabel.platform.presentation.screens.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.whitelabel.core.domain.model.DisplayableItem
import com.whitelabel.platform.utils.ExtractedColors
import com.whitelabel.platform.utils.LanguagePreferences

@Composable
fun <T : DisplayableItem> ItemThumbnailMarker(
    item: T,
    drawableResourceId: Int? = null,
    extractedColors: ExtractedColors? = null
) {
    val languageCode = LanguagePreferences.selectedLanguage.value?.code ?: "en"
    val context = LocalPlatformContext.current
    val imageUrl = item.imageUrls.firstOrNull()
    val imageModel = if (drawableResourceId != null && drawableResourceId != 0) drawableResourceId else imageUrl

    val borderColor = extractedColors?.getAccentColor() ?: Color.White

    Card(
        modifier = Modifier
            .size(60.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, borderColor)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageModel)
                .allowHardware(false)
                .build(),
            contentDescription = item.getLocalizedName(languageCode),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun <T : DisplayableItem> ItemInfoWindow(
    item: T,
    extractedColors: ExtractedColors? = null
) {
    val languageCode = LanguagePreferences.selectedLanguage.value?.code ?: "en"
    val backgroundColor = extractedColors?.getCardBackgroundColor() ?: MaterialTheme.colorScheme.surface
    val titleColor = extractedColors?.getTitleColor() ?: MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        Text(
            text = item.getLocalizedName(languageCode),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = titleColor
        )
        item.description?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.getLocalizedDescription(languageCode) ?: it,
                style = MaterialTheme.typography.bodySmall,
                color = titleColor.copy(alpha = 0.8f),
                maxLines = 2
            )
        }
    }
}
