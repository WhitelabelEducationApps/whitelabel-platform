package com.whitelabel.platform.utils

import android.os.Looper
import android.util.Log

/**
 * Android implementation of measureTimeAndLog.
 */
actual inline fun <T> measureTimeAndLog(tag: String, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val duration = System.currentTimeMillis() - start
    Log.d(tag, "Operation took ${duration}ms")
    return result
}

/**
 * Android implementation of checkMainThread.
 */
actual fun checkMainThread() {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        throw IllegalStateException("This operation must be called on the main thread")
    }
}
