package com.whitelabel.platform.utils

import android.os.SystemClock

/**
 * Android implementation of getCurrentTimeMillis.
 */
actual fun getCurrentTimeMillis(): Long {
    return SystemClock.elapsedRealtime()
}
