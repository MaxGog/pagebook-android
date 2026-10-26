package ru.maxgog.pagebook.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.maxgog.pagebook.R
import ru.maxgog.pagebook.ui.theme.AppTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(stringResource(R.string.settings_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        SettingsContent(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        )
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val resources = LocalContext.current.resources

    var selectedTheme by remember {
        mutableStateOf(AppTheme.themeMode.toThemeOption())
    }

    var selectedLanguage by remember {
        mutableStateOf(LanguageOption.fromLocale(Locale.getDefault()))
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SettingsSectionTitle(stringResource(R.string.theme_title))
        }

        item {
            ThemeSelection(
                selectedTheme = selectedTheme,
                onThemeSelected = { theme ->
                    selectedTheme = theme
                    AppTheme.themeMode = when (theme) {
                        ThemeOption.SYSTEM -> AppTheme.ThemeMode.SYSTEM
                        ThemeOption.LIGHT -> AppTheme.ThemeMode.LIGHT
                        ThemeOption.DARK -> AppTheme.ThemeMode.DARK
                    }
                }
            )
        }

        item {
            SettingsSectionTitle(stringResource(R.string.language_title))
        }

        item {
            LanguageSelection(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language ->
                    selectedLanguage = language
                    setAppLanguage(context, language.code)
                }
            )
        }

        item {
            SettingsSectionTitle(stringResource(R.string.app_description_title))
        }

        item {
            Text(
                text = stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = AppTheme.horizontalPadding,
                    vertical = 8.dp
                )
            )
        }
    }
}

private fun setAppLanguage(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = context.resources
    val configuration = resources.configuration
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)

    resources.updateConfiguration(configuration, resources.displayMetrics)

    // Перезапускаем приложение для применения языка
    (context as? android.app.Activity)?.recreate()
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            start = AppTheme.horizontalPadding,
            top = 16.dp,
            end = AppTheme.horizontalPadding,
            bottom = 8.dp
        )
    )
}

@Composable
private fun ThemeSelection(
    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.horizontalPadding)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .padding(vertical = 8.dp)
            ) {
                ThemeOption.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (theme == selectedTheme),
                                onClick = { onThemeSelected(theme) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (theme == selectedTheme),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Text(
                            text = when (theme) {
                                ThemeOption.SYSTEM -> stringResource(R.string.theme_system)
                                ThemeOption.LIGHT -> stringResource(R.string.theme_light)
                                ThemeOption.DARK -> stringResource(R.string.theme_dark)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSelection(
    selectedLanguage: LanguageOption,
    onLanguageSelected: (LanguageOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.horizontalPadding)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .padding(vertical = 8.dp)
            ) {
                LanguageOption.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (language == selectedLanguage),
                                onClick = { onLanguageSelected(language) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (language == selectedLanguage),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun AppTheme.ThemeMode.toThemeOption(): ThemeOption {
    return when (this) {
        AppTheme.ThemeMode.SYSTEM -> ThemeOption.SYSTEM
        AppTheme.ThemeMode.LIGHT -> ThemeOption.LIGHT
        AppTheme.ThemeMode.DARK -> ThemeOption.DARK
    }
}

private fun LanguageOption.Companion.fromLocale(locale: Locale): LanguageOption {
    return when (locale.language) {
        "ru" -> LanguageOption.RUSSIAN
        "en" -> LanguageOption.ENGLISH
        else -> LanguageOption.ENGLISH
    }
}

private enum class ThemeOption {
    SYSTEM, LIGHT, DARK
}

private enum class LanguageOption(val code: String, val displayName: String) {
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English");

    companion object {
        fun fromLocale(locale: Locale): LanguageOption {
            return when (locale.language) {
                "ru" -> RUSSIAN
                "en" -> ENGLISH
                else -> ENGLISH
            }
        }
    }
}