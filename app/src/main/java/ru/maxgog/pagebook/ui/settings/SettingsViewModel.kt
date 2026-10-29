package ru.maxgog.pagebook.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.maxgog.pagebook.BuildConfig
import ru.maxgog.pagebook.data.repository.SettingsRepository
import ru.maxgog.pagebook.ui.theme.AppTheme
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: AppTheme.ThemeMode = AppTheme.ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val versionName: String = BuildConfig.VERSION_NAME
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _language = MutableStateFlow(AppLanguage.current())

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        _language
    ) { theme, lang ->
        SettingsUiState(
            themeMode = theme,
            language = lang,
            versionName = BuildConfig.VERSION_NAME
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun setTheme(mode: AppTheme.ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setLanguage(language: AppLanguage) {
        _language.value = language
        AppLanguage.apply(language)
    }
}