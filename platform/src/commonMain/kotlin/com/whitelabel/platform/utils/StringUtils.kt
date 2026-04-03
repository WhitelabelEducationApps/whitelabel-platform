package com.whitelabel.platform.utils

/**
 * Sanitize a string to be used as a resource name by:
 * - Converting to lowercase
 * - Removing all special characters (spaces, apostrophes, quotes, commas, etc.)
 * - Keeping only alphanumeric characters
 * - Prepending 'a' if the name starts with a digit
 *
 * @param name The string to sanitize
 * @return Sanitized resource name
 */
fun sanitizeForResourceName(name: String): String {
    val sanitized = name.lowercase()
        .replace(Regex("[^a-z0-9]"), "")
    if (sanitized.firstOrNull()?.isDigit() == true) {
        return "a$sanitized"
    }
    return sanitized
}

/**
 * Sanitize a string to be used as a file name.
 * Removes special characters that are not safe for file systems.
 *
 * @param name The string to sanitize
 * @return Sanitized file name
 */
fun sanitizeForFileName(name: String): String {
    return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
}

/**
 * Truncate a string to a maximum length with ellipsis.
 *
 * @param maxLength Maximum length
 * @param ellipsis Ellipsis string (default "...")
 * @return Truncated string
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    if (this.length <= maxLength) return this
    return this.take(maxLength - ellipsis.length) + ellipsis
}

/**
 * Convert a string to camelCase.
 */
fun String.toCamelCase(): String {
    return this.split(Regex("[^a-zA-Z0-9]"))
        .filter { it.isNotEmpty() }
        .mapIndexed { index, word ->
            if (index == 0) word.lowercase()
            else word.capitalize()
        }
        .joinToString("")
}

/**
 * Convert a string to snake_case.
 */
fun String.toSnakeCase(): String {
    return this.split(Regex("[^a-zA-Z0-9]"))
        .filter { it.isNotEmpty() }
        .joinToString("_") { it.lowercase() }
}
