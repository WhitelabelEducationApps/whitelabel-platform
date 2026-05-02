package com.whitelabel.platform.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual object LocalizationManager {
    actual fun getCurrentLanguageCode(): String {
        return NSLocale.currentLocale.languageCode
    }
}
