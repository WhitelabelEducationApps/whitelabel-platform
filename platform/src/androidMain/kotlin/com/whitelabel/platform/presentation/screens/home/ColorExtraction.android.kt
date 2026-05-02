package com.whitelabel.platform.presentation.screens.home

import android.content.Context
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
import org.json.JSONObject

// ── Process-lifetime caches ──────────────────────────────────────────────────
// Both maps are only ever read/written on the Main thread (composition + collect).

/** Already-extracted colors keyed by siteId. Populated from precomputed cache or Palette. */
private val colorCache = HashMap<Long, ExtractedColors>()

/**
 * Colors pre-extracted at build time by scripts/extract_colors.py.
 * Keyed by drawable resource entry name (e.g. "acaipalm").
 * Null until first use; empty map if the asset is missing.
 */
private var precomputedCache: HashMap<String, ExtractedColors>? = null

// ── Asset loading ─────────────────────────────────────────────────────────────

private fun loadPrecomputed(context: Context): HashMap<String, ExtractedColors> {
    precomputedCache?.let { return it }
    val loaded = try {
        val json = context.assets.open("extracted_colors.json").bufferedReader().readText()
        parsePrecomputed(json)
    } catch (_: Exception) {
        HashMap()
    }
    precomputedCache = loaded
    return loaded
}

private fun parsePrecomputed(json: String): HashMap<String, ExtractedColors> {
    val map = HashMap<String, ExtractedColors>()
    try {
        val root = JSONObject(json)
        val keys = root.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val entry = root.getJSONObject(key)
            fun color(name: String): Color? =
                if (entry.isNull(name)) null else Color(entry.getInt(name))
            map[key] = ExtractedColors(
                vibrant      = color("vibrant"),
                vibrantDark  = color("vibrantDark"),
                vibrantLight = color("vibrantLight"),
                muted        = color("muted"),
                mutedDark    = color("mutedDark"),
                mutedLight   = color("mutedLight"),
                dominant     = color("dominant"),
            )
        }
    } catch (_: Exception) {}
    return map
}

// ── Composable ────────────────────────────────────────────────────────────────

@Composable
actual fun rememberExtractedColors(
    siteId: Long,
    drawableResourceId: Int?
): ExtractedColors? {
    val context = LocalContext.current

    // remember(siteId): initialise once per item. Checks both caches synchronously
    // so items with precomputed data never trigger a LaunchedEffect.
    var colors by remember(siteId) {
        // 1. Already extracted this session
        colorCache[siteId]?.let { return@remember mutableStateOf(it) }

        // 2. Pre-extracted at build time
        if (drawableResourceId != null && drawableResourceId != 0) {
            val name = context.resources.getResourceEntryName(drawableResourceId)
            loadPrecomputed(context)[name]?.let { precomputed ->
                colorCache[siteId] = precomputed
                return@remember mutableStateOf(precomputed)
            }
        }

        mutableStateOf(null)
    }

    // Only reached when the asset file has no entry for this drawable (new plants,
    // debug builds, or images added after the last release build).
    if (colors == null && drawableResourceId != null && drawableResourceId != 0) {
        LaunchedEffect(siteId, drawableResourceId) {
            val extracted = withContext(Dispatchers.Default) {
                try {
                    val drawable = ContextCompat.getDrawable(context, drawableResourceId)
                    val bitmap = drawable?.toBitmap(width = 100, height = 100)
                    if (bitmap != null) {
                        val palette = Palette.from(bitmap).generate()
                        ExtractedColors(
                            vibrant      = palette.vibrantSwatch?.rgb?.let { Color(it) },
                            vibrantDark  = palette.darkVibrantSwatch?.rgb?.let { Color(it) },
                            vibrantLight = palette.lightVibrantSwatch?.rgb?.let { Color(it) },
                            muted        = palette.mutedSwatch?.rgb?.let { Color(it) },
                            mutedDark    = palette.darkMutedSwatch?.rgb?.let { Color(it) },
                            mutedLight   = palette.lightMutedSwatch?.rgb?.let { Color(it) },
                            dominant     = palette.dominantSwatch?.rgb?.let { Color(it) },
                        )
                    } else null
                } catch (_: Exception) {
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
