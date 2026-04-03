package com.whitelabel.platform.utils

import androidx.compose.ui.graphics.Color

/**
 * Data class holding extracted colors from an image.
 * This is in commonMain so it can be used across all platforms.
 * The actual extraction functions are platform-specific (Android uses Palette API).
 */
data class ExtractedColors(
    val vibrant: Color? = null,
    val vibrantDark: Color? = null,
    val vibrantLight: Color? = null,
    val muted: Color? = null,
    val mutedDark: Color? = null,
    val mutedLight: Color? = null,
    val dominant: Color? = null
) {
    /**
     * Gets the best card background color from extracted colors.
     * Prefers muted colors for backgrounds as they're less distracting.
     */
    fun getCardBackgroundColor(fallback: Color = Color(0xFFF5F5F5)): Color {
        return mutedLight ?: muted ?: vibrantLight ?: dominant ?: fallback
    }
    
    /**
     * Gets the best title/text color that contrasts with the background.
     */
    fun getTitleColor(fallback: Color = Color.White): Color {
        return vibrant ?: vibrantDark ?: mutedDark ?: dominant ?: fallback
    }
    
    /**
     * Gets an accent color for highlights and buttons.
     */
    fun getAccentColor(fallback: Color = Color(0xFF6200EE)): Color {
        return vibrant ?: muted ?: dominant ?: fallback
    }
}
