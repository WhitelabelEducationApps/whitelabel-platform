package com.whitelabel.platform.presentation.screens.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.whitelabel.core.domain.model.DisplayableItem

/**
 * Wrapper class for DisplayableItem to work with Google Maps Clustering.
 * Implements ClusterItem interface for marker clustering support.
 *
 * @param item The displayable item with location data
 */
class ItemCluster<T : DisplayableItem>(val item: T) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(item.latitude ?: 0.0, item.longitude ?: 0.0)
    override fun getTitle(): String? = item.name
    override fun getSnippet(): String? = item.description
    override fun getZIndex(): Float? = null
}
