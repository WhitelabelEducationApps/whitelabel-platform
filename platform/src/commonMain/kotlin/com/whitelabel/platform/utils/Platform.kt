package com.whitelabel.platform.utils

import androidx.compose.runtime.Composable
import coil3.PlatformContext

@Composable
expect fun getPlatformContext(): PlatformContext

@Composable
expect fun getDrawableResourceId(name: String): Int

@Composable
expect fun getStringResource(id: String): String
