package ru.maxgog.pagebook.ui.settings

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ru.maxgog.pagebook.R

enum class AppLanguage(
    val tag: String?,
    @StringRes val labelRes: Int
) {
    SYSTEM(null, R.string.language_system_default),
    RUSSIAN("ru", R.string.language_ru),
    ENGLISH("en", R.string.language_en);

    companion object {
        fun current(): AppLanguage {
            val locales = AppCompatDelegate.getApplicationLocales()
            return if (locales.isEmpty) {
                SYSTEM
            } else {
                val currentTag = locales.toLanguageTags().substringBefore(',')
                entries.firstOrNull { it.tag == currentTag } ?: SYSTEM
            }
        }

        fun apply(language: AppLanguage) {
            val locales = if (language.tag == null) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(language.tag)
            }
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }
}