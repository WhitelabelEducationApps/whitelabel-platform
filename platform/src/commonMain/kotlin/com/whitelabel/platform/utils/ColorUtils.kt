package com.whitelabel.platform.utils

import androidx.compose.ui.graphics.Color
import com.whitelabel.platform.data.models.CatalogItem

/**
 * Convert an ARGB integer to a Compose Color.
 * Handles null values by returning a default color.
 */
fun toComposeColor(colorInt: Int?, default: Color = Color.Gray): Color {
    if (colorInt == null) return default
    return Color(colorInt)
}

/**
 * Blend two colors together with a given ratio.
 * @param color1 The first color
 * @param color2 The second color
 * @param ratio The ratio of color2 in the blend (0.0 to 1.0)
 * @return The blended color
 */
fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
    val inverseRatio = 1f - ratio
    return Color(
        red = color1.red * inverseRatio + color2.red * ratio,
        green = color1.green * inverseRatio + color2.green * ratio,
        blue = color1.blue * inverseRatio + color2.blue * ratio,
        alpha = color1.alpha * inverseRatio + color2.alpha * ratio
    )
}

/**
 * Calculate a blended background color for cards.
 * Uses the formula: blend(background, blend(primary, secondary, primarySecondaryRatio), finalBlendRatio)
 *
 * @param primaryColor Primary color as ARGB int
 * @param secondaryColor Secondary color as ARGB int
 * @param backgroundColor Background color as ARGB int
 * @param defaultPrimary Default primary color if null
 * @param defaultSecondary Default secondary color if null
 * @param defaultBackground Default background color if null
 * @param primarySecondaryRatio Blend ratio for primary/secondary (default 0.75)
 * @param finalBlendRatio Final blend ratio (default 0.2)
 * @return The blended card background color
 */
fun calculateCardBackgroundColor(
    primaryColor: Int?,
    secondaryColor: Int?,
    backgroundColor: Int?,
    defaultPrimary: Color = Color(0xFFE0E0E0),
    defaultSecondary: Color = Color(0xFFBDBDBD),
    defaultBackground: Color = Color(0xFFF5F5F5),
    primarySecondaryRatio: Float = 0.75f,
    finalBlendRatio: Float = 0.2f
): Color {
    val primary = toComposeColor(primaryColor, defaultPrimary)
    val secondary = toComposeColor(secondaryColor, defaultSecondary)
    val background = toComposeColor(backgroundColor, defaultBackground)

    // First blend primary and secondary
    val primarySecondaryBlend = blendColors(primary, secondary, primarySecondaryRatio)

    // Then blend the result with background
    return blendColors(background, primarySecondaryBlend, finalBlendRatio)
}

/**
 * Get the card background color for a CatalogItem.
 */
fun CatalogItem.getCardBackgroundColor(): Color {
    return calculateCardBackgroundColor(
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        backgroundColor = backgroundColor,
        defaultPrimary = Color(0xFFE0E0E0),
        defaultSecondary = Color(0xFFBDBDBD),
        defaultBackground = Color(0xFFF5F5F5),
        primarySecondaryRatio = 0.75f,
        finalBlendRatio = 0.2f
    )
}

