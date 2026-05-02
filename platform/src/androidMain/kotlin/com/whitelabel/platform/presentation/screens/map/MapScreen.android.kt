package com.whitelabel.platform.presentation.screens.map

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import com.whitelabel.core.domain.model.DisplayableItem
import com.whitelabel.platform.utils.ExtractedColors
import kotlinx.coroutines.delay

/**
 * Generic map screen implementation for Android with Google Maps.
 * Supports clustering, camera animations, and focused item navigation.
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun <T : DisplayableItem> MapScreen(
    items: List<T>,
    focusedItemId: Long? = null,
    onItemClick: (Long) -> Unit,
    onClearFocusedItem: () -> Unit = {},
    modifier: Modifier = Modifier,
    drawableResourceIdProvider: @Composable ((T) -> Int?)? = null,
    colorExtractor: @Composable ((T) -> ExtractedColors?)? = null
) {
    val itemsWithCoordinates = remember(items) {
        items.filter { it.latitude != null && it.longitude != null }
    }

    val focusedItem = remember(focusedItemId, itemsWithCoordinates) {
        focusedItemId?.let { id ->
            itemsWithCoordinates.find { it.id == id }
        }
    }

    if (itemsWithCoordinates.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Map view requires location data",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${items.size} items available, but no coordinates",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        val cameraPositionState = rememberCameraPositionState {
            val centerLat = itemsWithCoordinates.map { it.latitude!! }.average()
            val centerLng = itemsWithCoordinates.map { it.longitude!! }.average()
            position = CameraPosition.fromLatLngZoom(LatLng(centerLat, centerLng), 2f)
        }

        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false, minZoomPreference = 4.0f),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                rotationGesturesEnabled = true,
                tiltGesturesEnabled = true
            )
        ) {
            if (itemsWithCoordinates.isNotEmpty()) {
                Clustering(
                    items = itemsWithCoordinates.map { item -> ItemCluster(item) },
                    onClusterItemInfoWindowClick = { clusterItem ->
                        onItemClick(clusterItem.item.id)
                    },
                    clusterItemContent = { clusterItem: ItemCluster<T> ->
                        val drawableId = drawableResourceIdProvider?.invoke(clusterItem.item)
                        val colors = colorExtractor?.invoke(clusterItem.item)
                        ItemThumbnailMarker(
                            item = clusterItem.item,
                            drawableResourceId = drawableId,
                            extractedColors = colors
                        )
                    }
                )
            }

            // Animate to focused item
            LaunchedEffect(focusedItem) {
                val lat = focusedItem?.latitude
                val lng = focusedItem?.longitude
                if (lat != null && lng != null) {
                    delay(1000)
                    val targetPosition = LatLng(lat, lng)
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(targetPosition, 12f),
                        durationMs = 3000
                    )
                    onClearFocusedItem()
                }
            }
        }
    }
}
