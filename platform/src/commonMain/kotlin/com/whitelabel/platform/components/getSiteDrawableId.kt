package com.whitelabel.platform.components

import androidx.compose.runtime.Composable
import com.whitelabel.platform.data.models.CatalogItem

/**
 * Android implementation that uses getDrawableIdForSite to look up local drawable resources.
 */
@Composable
expect fun getSiteDrawableId(site: CatalogItem): Int?

/**
 * Returns all drawable resource IDs for a site: primary + numbered variants (_2.._6).
 * Probing stops at the first missing index.
 */
@Composable
expect fun getSiteDrawableIds(site: CatalogItem): List<Int>
