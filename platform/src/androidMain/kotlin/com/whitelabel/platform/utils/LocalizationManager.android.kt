package com.whitelabel.platform.utils

import java.util.Locale

actual object LocalizationManager {
    actual fun getCurrentLanguageCode(): String {
        return Locale.getDefault().language
    }
}
