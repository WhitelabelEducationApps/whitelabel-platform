package com.whitelabel.platform.utils

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLocationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit

expect fun hasLocationPermission(context: Any): Boolean
