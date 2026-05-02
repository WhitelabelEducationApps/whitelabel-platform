package com.whitelabel.platform.presentation

import com.whitelabel.core.domain.model.ItemGroup
import com.whitelabel.core.domain.repository.ItemRepository
import com.whitelabel.core.presentation.home.ItemGrouper
import com.whitelabel.platform.data.models.CatalogItem

/**
 * Groups plants by style (medicinal category). Each plant belongs to one style,
 * and style_ro/style_it/etc. translations are already in the item's localizedFields.
 */
class HeritageItemGrouper : ItemGrouper<CatalogItem> {

    override suspend fun group(
        items: List<CatalogItem>,
        repository: ItemRepository<CatalogItem>,
        languageCode: String
    ): List<ItemGroup<CatalogItem>> {
        val grouped = items.groupBy { it.style ?: "" }

        return grouped.entries
            .filter { it.key.isNotBlank() }
            .sortedBy { it.key }
            .map { (styleKey, sites) ->
                val displayName = sites.first()
                    .localizedFields.getCategory(languageCode)
                    ?: styleKey

                ItemGroup(
                    key = styleKey,
                    displayName = displayName,
                    items = sites.sortedBy { it.name }
                )
            }
    }
}
