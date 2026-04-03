package com.whitelabel.platform.utils

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Generic image preloader for efficient image caching.
 * Preloads images into memory cache for smooth scrolling performance.
 *
 * @param context Platform context for Coil
 * @param imageLoader Coil image loader instance
 * @param tag Optional tag for logging
 */
class ImagePreloader(
    private val context: PlatformContext,
    private val imageLoader: ImageLoader,
    private val tag: String = "ImagePreloader"
) {
    private val preloadedItems = mutableSetOf<String>()

    /**
     * Preload images for a list of items.
     *
     * @param items List of items to preload images for
     * @param currentIndex Current index in the list
     * @param preloadCount Number of items to preload ahead
     * @param imageUrlExtractor Function to extract image URL from item
     * @param cacheKeyExtractor Function to extract cache key from item
     * @param size Target image size for decoding
     */
    suspend fun <T> preloadImages(
        items: List<T>,
        currentIndex: Int,
        preloadCount: Int = 15,
        imageUrlExtractor: (T) -> String?,
        cacheKeyExtractor: (T) -> String,
        size: Pair<Int, Int> = 800 to 800
    ) = withContext(Dispatchers.IO) {
        val startIndex = currentIndex.coerceAtLeast(0)
        val endIndex = (currentIndex + preloadCount).coerceAtMost(items.size - 1)

        // Preload images in parallel
        val jobs = (startIndex..endIndex).mapNotNull { i ->
            val item = items.getOrNull(i) ?: return@mapNotNull null
            val imageUrl = imageUrlExtractor(item) ?: return@mapNotNull null
            val cacheKey = cacheKeyExtractor(item)

            // Skip if already preloaded
            if (preloadedItems.contains(cacheKey)) return@mapNotNull null
            preloadedItems.add(cacheKey)

            async {
                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .size(size.first, size.second)
                    .memoryCacheKey(cacheKey)
                    .diskCachePolicy(coil3.request.CachePolicy.DISABLED)
                    .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .build()

                try {
                    imageLoader.execute(request)
                } catch (e: Exception) {
                    // Ignore errors during preload
                }
            }
        }

        // Wait for all preloads to complete
        jobs.awaitAll()
    }

    /**
     * Preload a single image.
     *
     * @param imageUrl URL of the image to preload
     * @param cacheKey Cache key for the image
     * @param size Target image size
     */
    suspend fun preloadImage(
        imageUrl: String,
        cacheKey: String,
        size: Pair<Int, Int> = 800 to 800
    ) = withContext(Dispatchers.IO) {
        if (preloadedItems.contains(cacheKey)) return@withContext
        preloadedItems.add(cacheKey)

        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(size.first, size.second)
            .memoryCacheKey(cacheKey)
            .diskCachePolicy(coil3.request.CachePolicy.DISABLED)
            .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
            .build()

        try {
            imageLoader.execute(request)
        } catch (e: Exception) {
            // Ignore errors during preload
        }
    }

    /**
     * Clear the preloaded items cache.
     */
    fun clearCache() {
        preloadedItems.clear()
    }

    /**
     * Check if an item has been preloaded.
     */
    fun isPreloaded(cacheKey: String): Boolean {
        return preloadedItems.contains(cacheKey)
    }
}

/**
 * Generate a thumbnail cache key for an item.
 */
fun getThumbnailCacheKey(itemId: Long, prefix: String = "item"): String {
    return "${prefix}_${itemId}_thumbnail"
}

/**
 * Generate a full image cache key for an item.
 */
fun getFullImageCacheKey(itemId: Long, prefix: String = "item"): String {
    return "${prefix}_${itemId}_full"
}
