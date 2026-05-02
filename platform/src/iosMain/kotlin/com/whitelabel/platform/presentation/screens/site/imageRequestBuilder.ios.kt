package com.whitelabel.platform.presentation.screens.site

import androidx.compose.runtime.Composable
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
actual fun imageRequestBuilder(): ImageRequest.Builder {
    return ImageRequest.Builder(LocalPlatformContext.current)
}
