package ru.maxgog.pagebook.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxgog.pagebook.ui.theme.AppTheme
import javax.inject.Inject
import javax.inject.Singleton

private val THEME_KEY = stringPreferencesKey("theme_mode")

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val themeMode: Flow<AppTheme.ThemeMode> = dataStore.data.map { prefs ->
        prefs[THEME_KEY]?.let { AppTheme.ThemeMode.valueOf(it) }
            ?: AppTheme.ThemeMode.SYSTEM
    }

    suspend fun setThemeMode(mode: AppTheme.ThemeMode) {
        dataStore.edit { it[THEME_KEY] = mode.name }
    }
}