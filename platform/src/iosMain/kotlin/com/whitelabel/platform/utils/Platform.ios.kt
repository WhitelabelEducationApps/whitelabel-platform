package com.whitelabel.platform.utils

import androidx.compose.runtime.Composable
import coil3.PlatformContext

import coil3.compose.LocalPlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = LocalPlatformContext.current

@Composable
actual fun getDrawableResourceId(name: String): Int = 0

@Composable
actual fun getStringResource(id: String): String = id

actual fun restartActivity() {
    // Stub
}
