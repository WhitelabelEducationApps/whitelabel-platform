package com.whitelabel.platform.utils

import co.touchlab.kermit.Logger as KermitLogger
import co.touchlab.kermit.Severity

/**
 * Generic logger for whitelabel applications.
 * Wraps Kermit logger with configurable tag.
 */
class Logger(tag: String = "WhitelabelApp") {
    private val logger = KermitLogger.withTag(tag)

    fun d(message: String) {
        logger.d { message }
    }

    fun i(message: String) {
        logger.i { message }
    }

    fun w(message: String) {
        logger.w { message }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.e(throwable) { message }
        } else {
            logger.e { message }
        }
    }

    fun v(message: String) {
        logger.v { message }
    }
}

/**
 * Convenience function for logging with default logger.
 */
fun LOG(message: String, level: LogLevel = LogLevel.DEBUG, tag: String = "WhitelabelApp") {
    val logger = Logger(tag)
    when (level) {
        LogLevel.VERBOSE -> logger.v(message)
        LogLevel.DEBUG -> logger.d(message)
        LogLevel.INFO -> logger.i(message)
        LogLevel.WARN -> logger.w(message)
        LogLevel.ERROR -> logger.e(message)
    }
}

fun LOG(message: String, throwable: Throwable, tag: String = "WhitelabelApp") {
    Logger(tag).e(message, throwable)
}

enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}

/**
 * Performance monitoring utility - platform-specific implementations
 */
expect inline fun <T> measureTimeAndLog(tag: String, block: () -> T): T

/**
 * Thread checker utility - platform-specific implementations
 */
expect fun checkMainThread()
