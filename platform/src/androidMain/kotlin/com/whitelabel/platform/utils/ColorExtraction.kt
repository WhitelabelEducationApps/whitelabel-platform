package com.whitelabel.platform.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Extracts colors from an image resource using the Palette API.
 *
 * @param context Android context
 * @param drawableResourceId The drawable resource ID to extract colors from
 * @return ExtractedColors containing vibrant, muted, and dominant colors
 */
suspend fun extractColorsFromDrawable(
    context: Context,
    drawableResourceId: Int
): ExtractedColors = withContext(Dispatchers.Default) {
    val drawable = try {
        context.getDrawable(drawableResourceId)
    } catch (e: Exception) {
        null
    }
    
    val bitmap = drawable?.toBitmap(width = 100, height = 100)
    
    if (bitmap != null) {
        val palette = Palette.from(bitmap).generate()
        
        val vibrant = palette.vibrantSwatch?.rgb?.toComposeColor()
        val vibrantDark = palette.darkVibrantSwatch?.rgb?.toComposeColor()
        val vibrantLight = palette.lightVibrantSwatch?.rgb?.toComposeColor()
        val muted = palette.mutedSwatch?.rgb?.toComposeColor()
        val mutedDark = palette.darkMutedSwatch?.rgb?.toComposeColor()
        val mutedLight = palette.lightMutedSwatch?.rgb?.toComposeColor()
        val dominant = palette.dominantSwatch?.rgb?.toComposeColor()
        
        ExtractedColors(
            vibrant = vibrant,
            vibrantDark = vibrantDark,
            vibrantLight = vibrantLight,
            muted = muted,
            mutedDark = mutedDark,
            mutedLight = mutedLight,
            dominant = dominant
        )
    } else {
        ExtractedColors()
    }
}

/**
 * Extracts colors from an image URL using the Palette API.
 *
 * @param context Android context
 * @param imageUrl The image URL to extract colors from
 * @return ExtractedColors containing vibrant, muted, and dominant colors
 */
suspend fun extractColorsFromUrl(
    context: Context,
    imageUrl: String
): ExtractedColors = withContext(Dispatchers.Default) {
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(100, 100)
        .build()
    
    val result = context.imageLoader.execute(request)
    val drawable = result.image?.asDrawable(context.resources)
    
    val bitmap = drawable?.toBitmap(width = 100, height = 100)
    
    if (bitmap != null) {
        val palette = Palette.from(bitmap).generate()
        
        val vibrant = palette.vibrantSwatch?.rgb?.toComposeColor()
        val vibrantDark = palette.darkVibrantSwatch?.rgb?.toComposeColor()
        val vibrantLight = palette.lightVibrantSwatch?.rgb?.toComposeColor()
        val muted = palette.mutedSwatch?.rgb?.toComposeColor()
        val mutedDark = palette.darkMutedSwatch?.rgb?.toComposeColor()
        val mutedLight = palette.lightMutedSwatch?.rgb?.toComposeColor()
        val dominant = palette.dominantSwatch?.rgb?.toComposeColor()
        
        ExtractedColors(
            vibrant = vibrant,
            vibrantDark = vibrantDark,
            vibrantLight = vibrantLight,
            muted = muted,
            mutedDark = mutedDark,
            mutedLight = mutedLight,
            dominant = dominant
        )
    } else {
        ExtractedColors()
    }
}

/**
 * Convert an Android color int to Compose Color.
 */
private fun Int.toComposeColor(): Color {
    return Color(this)
}
