package com.whitelabel.platform.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.whitelabel.platform.data.models.CatalogItem
import com.whitelabel.platform.utils.getDrawableIdForSite
import com.whitelabel.platform.utils.toDrawableResourceName

import androidx.compose.runtime.remember

/**
 * Android implementation that uses getDrawableIdForSite to look up local drawable resources.
 */
@Composable
actual fun getSiteDrawableId(site: CatalogItem): Int? {
    val context = LocalContext.current
    return remember(site.id) {
        context.getDrawableIdForSite(site.name)
    }
}

/**
 * Returns all drawable resource IDs for a site: primary + numbered variants (_2.._6).
 * Probing stops at the first missing index.
 */
@Composable
actual fun getSiteDrawableIds(site: CatalogItem): List<Int> {
    val context = LocalContext.current
    return remember(site.id) {
        val baseName = site.name.toDrawableResourceName()
        val ids = mutableListOf<Int>()
        val primary = context.resources.getIdentifier(baseName, "drawable", context.packageName)
        if (primary != 0) ids.add(primary)
        for (i in 2..6) {
            val id = context.resources.getIdentifier("${baseName}_$i", "drawable", context.packageName)
            if (id != 0) ids.add(id) else break
        }
        ids
    }
}
