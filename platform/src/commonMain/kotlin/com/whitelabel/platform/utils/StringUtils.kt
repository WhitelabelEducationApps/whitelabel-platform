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

