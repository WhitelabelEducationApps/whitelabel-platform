package com.whitelabel.platform.utils

import androidx.compose.runtime.Composable

@Composable
actual fun rememberLocationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit = { /* iOS: not implemented */ }

actual fun hasLocationPermission(context: Any): Boolean = false
