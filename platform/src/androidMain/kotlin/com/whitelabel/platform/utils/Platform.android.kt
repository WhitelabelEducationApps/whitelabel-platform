package com.whitelabel.platform.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil3.PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = LocalContext.current

@Composable
actual fun getDrawableResourceId(name: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}

@Composable
actual fun getStringResource(id: String): String {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(id, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else id
}

actual fun restartActivity() {
    // We can't easily get the activity here without a context.
    // In a real app, we might use a global Activity provider or similar.
    // For now, let's see if we can use getContext() if it exists.
}
