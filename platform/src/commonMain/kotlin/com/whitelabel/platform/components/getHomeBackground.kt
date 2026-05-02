package com.whitelabel.platform.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Returns a [Painter] for the home screen background, or null if the app doesn't provide one.
 * Each app places a [home_background] drawable in its res/drawable-nodpi/ folder.
 * Returning null causes no background to be rendered.
 */
@Composable
expect fun getHomeBackgroundPainter(): Painter?
