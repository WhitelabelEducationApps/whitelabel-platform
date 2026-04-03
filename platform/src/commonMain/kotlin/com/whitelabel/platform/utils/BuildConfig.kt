package com.whitelabel.platform.utils

/**
 * Build configuration for the whitelabel platform.
 * Set this from your app's initialization to control debug behavior.
 */
object BuildConfig {
    /**
     * Set to true to enable debug logging.
     * Should be set from your app's BuildConfig.DEBUG in initialization.
     */
    var DEBUG: Boolean = true
    
    /**
     * Application version name.
     */
    var VERSION_NAME: String = "0.1.0"
    
    /**
     * Application version code.
     */
    var VERSION_CODE: Int = 1
    
    /**
     * Build type (e.g., "debug", "release").
     */
    var BUILD_TYPE: String = "debug"
}

/**
 * Executes the given block only if in debug mode.
 */
inline fun debugOnly(block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}

/**
 * Logs a verbose message only in debug mode.
 */
fun debugLogV(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        LOG("[$tag] $message", LogLevel.VERBOSE)
    }
}

/**
 * Logs a debug message only in debug mode.
 */
fun debugLogD(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        LOG("[$tag] $message", LogLevel.DEBUG)
    }
}

/**
 * Logs an info message only in debug mode.
 */
fun debugLogI(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        LOG("[$tag] $message", LogLevel.INFO)
    }
}

/**
 * Logs the entry into a function.
 */
inline fun <T> logFunctionEntry(tag: String, functionName: String, block: () -> T): T {
    debugLogD(tag, "→ Entering $functionName")
    val startTime = getCurrentTimeMillis()
    return try {
        val result = block()
        val duration = getCurrentTimeMillis() - startTime
        debugLogD(tag, "← Exiting $functionName (took ${duration}ms)")
        result
    } catch (e: Exception) {
        val duration = getCurrentTimeMillis() - startTime
        LOG("[$tag] ✕ $functionName failed after ${duration}ms: ${e.message}", LogLevel.ERROR)
        throw e
    }
}

/**
 * Logs state changes.
 */
fun <T> logStateChange(tag: String, propertyName: String, oldValue: T?, newValue: T) {
    debugLogD(tag, "⟲ $propertyName: $oldValue → $newValue")
}

/**
 * Logs a user action.
 */
fun logUserAction(tag: String, action: String, details: String? = null) {
    val message = if (details != null) "👤 User $action | $details" else "👤 User $action"
    debugLogI(tag, message)
}

/**
 * Logs lifecycle events.
 */
fun logLifecycle(tag: String, event: String) {
    debugLogD(tag, "🔄 Lifecycle: $event")
}

/**
 * Logs database operations.
 */
fun logDatabase(tag: String, operation: String, details: String? = null) {
    val message = if (details != null) "🗄️ DB $operation | $details" else "🗄️ DB $operation"
    debugLogD(tag, message)
}

/**
 * Logs network operations.
 */
fun logNetwork(tag: String, operation: String, details: String? = null) {
    val message = if (details != null) "🌐 Network $operation | $details" else "🌐 Network $operation"
    debugLogD(tag, message)
}

/**
 * Logs image loading operations.
 */
fun logImageLoad(tag: String, operation: String, url: String? = null) {
    val message = if (url != null) "🖼️ Image $operation | $url" else "🖼️ Image $operation"
    debugLogD(tag, message)
}

/**
 * Gets current time in milliseconds for performance tracking.
 * Platform-specific implementation.
 */
expect fun getCurrentTimeMillis(): Long
