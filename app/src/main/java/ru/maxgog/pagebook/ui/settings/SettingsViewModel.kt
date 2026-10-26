package ru.maxgog.pagebook.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.data.repository.SettingsRepository
import ru.maxgog.pagebook.ui.theme.AppTheme
import java.util.Locale
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: AppTheme.ThemeMode = AppTheme.ThemeMode.SYSTEM,
    val language: LanguageOption = LanguageOption.RUSSIAN,
    val versionName: String = "3.0.0" //BuildConfig.VERSION_NAME
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.language
    ) { theme, lang ->
        SettingsUiState(
            themeMode = theme,
            language = lang,
            versionName = "3.0.0" //BuildConfig.VERSION_NAME
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun setTheme(mode: AppTheme.ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setLanguage(language: LanguageOption) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(language.code)
            )
        }
    }
}