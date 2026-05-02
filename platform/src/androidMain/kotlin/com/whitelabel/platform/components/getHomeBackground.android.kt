package com.whitelabel.platform.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
actual fun getHomeBackgroundPainter(): Painter? {
    val context = LocalContext.current
    val id = remember {
        context.resources.getIdentifier("home_background", "drawable", context.packageName)
    }
    return if (id != 0) painterResource(id) else null
}
