package com.whitelabel.platform.presentation.screens.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.whitelabel.platform.utils.ExtractedColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Process-lifetime cache: extraction runs at most once per item id.
// Reads/writes happen on the Main thread so a plain HashMap is safe.
private val colorCache = HashMap<Long, ExtractedColors>()

@Composable
actual fun rememberExtractedColors(
    siteId: Long,
    drawableResourceId: Int?
): ExtractedColors? {
    val context = LocalContext.current
    // Initialise directly from cache — avoids a recomposition on scroll-back.
    var colors by remember(siteId) { mutableStateOf(colorCache[siteId]) }

    if (colors == null && drawableResourceId != null && drawableResourceId != 0) {
        LaunchedEffect(siteId, drawableResourceId) {
            val extracted = withContext(Dispatchers.Default) {
                try {
                    val drawable = ContextCompat.getDrawable(context, drawableResourceId)
                    val bitmap = drawable?.toBitmap(width = 100, height = 100)
                    if (bitmap != null) {
                        val palette = Palette.from(bitmap).generate()
                        ExtractedColors(
                            vibrant = palette.vibrantSwatch?.rgb?.let { Color(it) },
                            vibrantDark = palette.darkVibrantSwatch?.rgb?.let { Color(it) },
                            vibrantLight = palette.lightVibrantSwatch?.rgb?.let { Color(it) },
                            muted = palette.mutedSwatch?.rgb?.let { Color(it) },
                            mutedDark = palette.darkMutedSwatch?.rgb?.let { Color(it) },
                            mutedLight = palette.lightMutedSwatch?.rgb?.let { Color(it) },
                            dominant = palette.dominantSwatch?.rgb?.let { Color(it) }
                        )
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            if (extracted != null) {
                colorCache[siteId] = extracted
                colors = extracted
            }
        }
    }

    return colors
}

private fun Drawable.toBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, width, height)
    draw(canvas)
    return bitmap
}
