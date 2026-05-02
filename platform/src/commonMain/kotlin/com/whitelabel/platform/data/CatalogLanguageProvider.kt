package com.whitelabel.platform.data

import com.whitelabel.core.domain.language.LanguageProvider
import com.whitelabel.platform.utils.LanguagePreferences
import com.whitelabel.platform.utils.SupportedLanguage
import kotlinx.coroutines.flow.StateFlow

class CatalogLanguageProvider : LanguageProvider {
    override fun getCurrentLanguageCode(): String = LanguagePreferences.getEffectiveLanguage()

    override val selectedLanguage: StateFlow<String?>
        get() = object : StateFlow<String?> {
            override val value: String? get() = LanguagePreferences.selectedLanguage.value?.code
            override val replayCache: List<String?> get() = listOf(value)
            override suspend fun collect(collector: kotlinx.coroutines.flow.FlowCollector<String?>): Nothing {
                LanguagePreferences.selectedLanguage.collect { lang ->
                    collector.emit(lang?.code)
                }
            }
        }

    override fun setLanguage(code: String?) {
        LanguagePreferences.setLanguage(code?.let { SupportedLanguage.fromCode(it) })
    }
}
